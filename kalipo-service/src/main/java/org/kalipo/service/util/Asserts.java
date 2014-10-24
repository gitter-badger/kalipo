package org.kalipo.service.util;

import org.apache.commons.lang3.StringUtils;
import org.kalipo.config.ErrorCode;
import org.kalipo.domain.Thread;
import org.kalipo.security.SecurityUtils;
import org.kalipo.web.rest.KalipoRequestException;

/**
 * Created by damoeb on 26.09.14.
 */
public final class Asserts {
    public static void isNotNull(Object o, String paramName) throws KalipoRequestException {
        if (o == null) {
            throw new KalipoRequestException(ErrorCode.INVALID_PARAMETER, paramName);
        }

    }

    public static void isNull(Object o, String paramName) throws KalipoRequestException {
        if (o != null) {
            throw new KalipoRequestException(ErrorCode.INVALID_PARAMETER, paramName);
        }
    }

    public static void hasPrivilege(String privilege) throws KalipoRequestException {
        if (!SecurityUtils.hasPrivilege(privilege)) {
            throw new KalipoRequestException(ErrorCode.PERMISSION_DENIED, "uriHook");
        }
    }

    public static void isNotReadOnly(Thread thread) throws KalipoRequestException {
        if (thread.getReadOnly()) {
            throw new KalipoRequestException(ErrorCode.CONSTRAINT_VIOLATED, "thread is readonly");
        }
    }

    public static void nullOrEqual(String found, String expected, String fieldName) throws KalipoRequestException {
        if (!StringUtils.equals(found, expected)) {
            throw new KalipoRequestException(ErrorCode.CONSTRAINT_VIOLATED, String.format("%s cannot be modified", fieldName));
        }
    }

    public static void nullOrEqual(Integer found, Integer expected, String fieldName) throws KalipoRequestException {
        if (found != null && !found.equals(expected)) {
            throw new KalipoRequestException(ErrorCode.CONSTRAINT_VIOLATED, String.format("%s cannot be modified", fieldName));
        }
    }
}
