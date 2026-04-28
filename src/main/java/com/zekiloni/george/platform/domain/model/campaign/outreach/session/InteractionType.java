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
    SUBMIT_RETRY,

    // Validation
    VALIDATION_ERROR,

    // Heartbeat
    HEARTBEAT,

    // Realtime responses
    MODAL_RESPONSE,
    COMMAND_RECEIVED,

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

    // Session control
    BLOCK_SESSION,
    KICK_SESSION,

    // System
    ERROR,
    WARNING,
    INFO,

    CUSTOM_EVENT,
}
