package com.zekiloni.george.platform.application.port.in.campaign;

/**
 * Records the simulator's bot-gate verdict on an outreach. Called when the
 * simulator refuses to render the visitor's page because the request looked
 * like a bot/scraper/abuse pattern. Stamps {@code outreach.kick_reason} so
 * the campaign Analytics tab can show a bot-rejection rate over time.
 *
 * <p>Doesn't create a UserSession — the visitor never gets that far.
 */
public interface OutreachKickUseCase {

    void kick(String token, String reason);
}
