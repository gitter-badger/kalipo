'use strict';

kalipoApp.controller('ViewThreadController', ['$scope', '$routeParams', '$rootScope', 'Thread', 'Comment', 'Report', 'Vote', '$log', '$location', '$anchorScroll',
    function ($scope, $routeParams, $rootScope, Thread, Comment, Report, Vote, $log, $location, $anchorScroll) {
        if ($location.path().endsWith('share')) {
            $scope.more = 'social';
        } else {
            $scope.more = 'details';
        }

        $scope.draft = {};
        $scope.thread = {};
        $scope.reportModel = {};
        $scope.$doComment = false;
        $scope.$multiline = false;

        var threadId = $routeParams.threadId;
        var commentId = $routeParams.commentId;

        Thread.get({id: threadId}, function (thread) {
            $scope.thread = thread;
        });

        Thread.getComments({id: threadId}, function (comments) {

            $scope.comments = [];
            $scope.pending = [];

            var groups = _.groupBy(_.sortBy(comments, function(comment){return -comment.createdDate}), function(comment) {
                return comment.status == 'PENDING' ? 'PENDING' : 'OTHERS';
            });

            $scope.comments = _hierarchical(groups.OTHERS);
            $scope.pending = groups.PENDING;

//            todo enable scrollTo
//            if (commentId) {
//                noty({text: 'Go to comment ' + commentId});
//                $scope.scrollTo(commentId);
//            }
        });

        var isTyping = false;
        var stoppedTypingTimer = 0;
        $scope.onTyping = function () {
            if (!isTyping) {
                console.log('started typing');
                isTyping = true;
                $rootScope.liveRequest.sendMessage(isTyping, threadId);
            }

            if (stoppedTypingTimer) {
                clearInterval(stoppedTypingTimer);
            }
            stoppedTypingTimer = setTimeout(function () {
                isTyping = false;
                console.log('stopped typing');
                $rootScope.liveRequest.sendMessage(isTyping, threadId);
            }, 10000);
        };

        $scope.toggleShareComponent = function () {
            if ($scope.more == null) {
                $scope.more = 'social';
            } else {
                $scope.more = null;
            }
        };

        $scope.create = function () {

            $scope.draft.threadId = threadId;

            Comment.save($scope.draft,
                function () {
                    $scope.clear();
                });
        };

        var _hierarchical = function (comments) {

            var map = _.groupBy(comments, function(comment) {
                comment.children = [];
                comment.$report = false;
                comment.$commentCount = 1;

                if (comment.hidden) {
                    comment.text = 'Content hidden';
                    comment.dislikes = 0;
                    comment.likes = 0;
                }

                if (comment.status == 'DELETED') {
                    comment.authorId = 'Deleted';
                    comment.text = 'Content deleted';
                    comment.dislikes = 0;
                    comment.likes = 0;
                }

                // todo minimize negative-only comments, hell-banned subthreads

                comment.$maximized = !(comment.dislikes > 2 && comment.dislikes > comment.likes);

                // todo wenn mehr als 3 kommentare zeige nur die relevaten 3 an all "23 kommentare anzeigen"

                var total = comment.likes + comment.dislikes;
                comment.$likes = comment.likes / total * 100;
                comment.$dislikes = comment.dislikes / total * 100;

                return comment.id;
            });

            return _.filter(comments, function(comment) {
                if (comment.parentId == null) {
                    return true;
                } else {
                    var parent = map[comment.parentId][0];
                    parent.children.push(comment);

                    // push commentCount to parents
                    var child = comment;
                    while(child.parentId) {
                        parent = map[child.parentId][0];
                        parent.$commentCount += 1;
                        child = parent;
                    }
                }
            });
        };

        $scope.toggleReplyForm = function (comment) {
            $scope.$replyTo = comment.id;
            comment.report = false;
        };

        $scope.toggleReportForm = function (comment) {
            $scope.$replyTo = null;
            comment.report = !comment.report;
        };

        $scope.report = function (comment) {
            comment.report = false;

            $scope.reportModel.commentId = comment.id;

            Report.save($scope.reportModel,
                function () {
                    $scope.report.reason = null;
                });
        };

        $scope.like = function (comment) {
            comment.likes++;

            var vote = {isLike: true, commentId: comment.id};

            Vote.save(vote, function (updated) {
//                noty({text: 'Liked', type: 'success'});
            });
        };

        $scope.cancelRelyTo = function () {
            $scope.draft.parentId = null;
        };

        $scope.dislike = function (comment) {
            comment.dislikes++;

            var vote = {isLike: false, commentId: comment.id};

            Vote.save(vote, function (updated) {
//                noty({text: 'Disliked', type: 'success'});
            });
        };

        $scope.clear = function () {
            $scope.draft = {id: null, text: null};
        };

        // --

        $scope.typing = [];

        $scope.threadEventSocket = atmosphere;
        $scope.threadEventSubSocket;
        $scope.threadEventTransport = 'websocket';

        $scope.threadEventRequest = { url: 'websocket/live/channel',
            contentType: "application/json",
            transport: $scope.threadEventTransport,
            trackMessageLength: true,
            reconnectInterval: 5000,
            enableXDR: true,
            timeout: 60000 };

        $scope.threadEventRequest.onOpen = function (response) {
            $scope.threadEventTransport = response.transport;
            $scope.threadEventRequest.uuid = response.request.uuid;
        };

        $scope.threadEventRequest.onMessage = function (response) {
            var message = response.responseBody;
            var event = atmosphere.util.parseJSON(message);

            if (event.threadId == threadId) {
                if (event.typing) {
                    $scope.typing.push(event.userLogin);
                    $scope.typing = _.sortBy(_.compact($scope.typing), 'user');
                } else {
                    $scope.typing = _.remove($scope.typing, function (userLogin) {
                        return userLogin != event.userLogin;
                    })
                }
            }

            $scope.$apply();
        };

        $scope.threadEventSubSocket = $scope.threadEventSocket.subscribe($scope.threadEventRequest);

        // --

        $scope.scrollTo = function (id) {
            var old = $location.hash();
            $location.hash(id);
            $anchorScroll();
            //reset to old to keep any additional routing logic from kicking in
            $location.hash(old);
        };

    }]);