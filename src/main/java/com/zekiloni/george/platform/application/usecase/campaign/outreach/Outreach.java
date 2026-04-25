package com.zekiloni.george.platform.application.usecase.campaign.outreach;

import java.util.List;

public class Outreach {
    private String id;
    private String sessionToken;
    private String recipient;
    private String message;
    private OutreachStatus status;
    private List<InteractionSignal> signals;
}
