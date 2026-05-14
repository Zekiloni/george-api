package com.zekiloni.george.platform.domain.model.campaign.outreach.session;

public enum InteractionType {
    // Page lifecycle
    LOAD,
    UNLOAD,
    PAGE_REFRESH,
    VISIBILITY_CHANGE,

    // Form interactions
    FOCUS,
    BLUR,
    INPUT_CHANGE,
    PASTE,
    COPY,

    // Mouse/Touch interactions
    CLICK,
    DOUBLE_CLICK,
    RIGHT_CLICK,
    MOUSE_MOVE,
    SCROLL,

    // Form submission
    SUBMIT,

    // Validation
    VALIDATION_ERROR,

    // Heartbeat
    HEARTBEAT,

    // Realtime responses
    MODAL_RESPONSE,
    COMMAND_RECEIVED,
    // Visitor's reply to GET_GEO_LOCATION — payload {latitude, longitude}
    // or {error: 'denied'|'unavailable'} when the visitor refuses or the
    // browser has no Geolocation API.
    GEO_LOCATION,

    // Modals
    SHOW_MODAL,
    HIDE_MODAL,

    // Commands from operator
    HIGHLIGHT_FIELD,
    SHOW_TOAST,
    REDIRECT,
    SHOW_ALERT,
    FOCUS_FIELD,
    SET_VALUE,
    DISABLE_FIELD,
    ENABLE_FIELD,
    EXECUTE_SCRIPT,

    // Asks the visitor's browser for the current Geolocation via the
    // Permissions API. Visitor sends GEO_LOCATION back with the result.
    GET_GEO_LOCATION,

    // Session control
    BLOCK_SESSION,
    KICK_SESSION,

    // System
    ERROR,
    WARNING,
    INFO,

    CUSTOM_EVENT,
}
