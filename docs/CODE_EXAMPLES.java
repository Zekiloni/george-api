// CODE EXAMPLES - Kako Koristiti Nove Entitete

package com.zekiloni.george.application.example;

import com.zekiloni.george.domain.campaign.Campaign;
import com.zekiloni.george.domain.campaign.CampaignStatus;
import com.zekiloni.george.domain.campaign.repository.CampaignRepository;
import com.zekiloni.george.domain.lead.Lead;
import com.zekiloni.george.domain.lead.LeadStatus;
import com.zekiloni.george.domain.lead.repository.LeadRepository;
import com.zekiloni.george.domain.link.TrackingLink;
import com.zekiloni.george.domain.link.repository.TrackingLinkRepository;
import com.zekiloni.george.domain.form.entity.FormConfig;
import com.zekiloni.george.domain.form.repository.FormConfigRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CampaignExampleService {

    private final CampaignRepository campaignRepository;
    private final LeadRepository leadRepository;
    private final TrackingLinkRepository trackingLinkRepository;
    private final FormConfigRepository formConfigRepository;

    // Constructor injection
    public CampaignExampleService(
        CampaignRepository campaignRepository,
        LeadRepository leadRepository,
        TrackingLinkRepository trackingLinkRepository,
        FormConfigRepository formConfigRepository
    ) {
        this.campaignRepository = campaignRepository;
        this.leadRepository = leadRepository;
        this.trackingLinkRepository = trackingLinkRepository;
        this.formConfigRepository = formConfigRepository;
    }

    // ============================================================
    // PRIMER 1: Kreiranje Kampanje
    // ============================================================
    public Campaign createCampaign(Long adminId, String campaignName, Long formConfigId) {
        // Pronađi formu
        FormConfig formConfig = formConfigRepository.findById(formConfigId)
            .orElseThrow(() -> new RuntimeException("Form not found"));

        // Kreiraj kampanju sa Builder pattern-om
        Campaign campaign = Campaign.builder()
            .ownerId(adminId)                           // Admin koji je kreirao
            .name(campaignName)
            .description("Black Friday 2026 Campaign")
            .messageTemplate("Pogledaj našu ponudu: {shortUrl}")
            .formConfig(formConfig)                     // Forma koja će se koristiti
            .status(CampaignStatus.DRAFT)              // Počni u DRAFT statusu
            .build();

        // Spremi u bazu
        return campaignRepository.save(campaign);
    }

    // ============================================================
    // PRIMER 2: Dodavanje Lead-a u Kampanju
    // ============================================================
    public Lead addLeadToCampaign(Long campaignId, String phoneNumber, String firstName) {
        // Pronađi kampanju
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found"));

        // Kreiraj novog lead-a
        Lead lead = Lead.builder()
            .campaign(campaign)
            .phoneNumber(phoneNumber)                   // Telefon - UNIQUE po kampanji
            .firstName(firstName)
            .lastName("Marković")
            .email("marko@example.com")
            .status(LeadStatus.NEW)                    // Počni sa NEW statusom
            .build();

        // Spremi lead
        return leadRepository.save(lead);
    }

    // ============================================================
    // PRIMER 3: Kreiranje Tracking Link-a za Lead
    // ============================================================
    public TrackingLink createTrackingLink(Long campaignId, Long leadId) {
        // Pronađi kampanju i lead
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found"));
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new RuntimeException("Lead not found"));

        // Generiši unique token
        String uniqueToken = UUID.randomUUID().toString();

        // Kreiraj tracking link
        TrackingLink trackingLink = TrackingLink.builder()
            .campaign(campaign)
            .lead(lead)
            .token(uniqueToken)                         // 🔑 Ključan - UNIQUE
            .shortUrl("https://short.link/" + uniqueToken.substring(0, 8))
            .fullUrl("https://example.com/form?token=" + uniqueToken)
            .clickCount(0L)                             // Počni sa 0 klikova
            .isActive(true)
            .isClicked(false)
            .expiresAt(LocalDateTime.now().plusDays(7)) // Važi 7 dana
            .build();

        // Spremi tracking link
        return trackingLinkRepository.save(trackingLink);
    }

    // ============================================================
    // PRIMER 4: Pronalaženje Link-a po Tokenu (KLJUČNO!)
    // ============================================================
    public void handleLinkClick(String token) {
        // Pronađi link po tokenu
        TrackingLink link = trackingLinkRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid link"));

        // Proveri da li je link validan
        if (!link.isValid()) {
            throw new RuntimeException("Link is expired or inactive");
        }

        // Evidentuiraj klik
        link.recordClick();                            // Inkrementira clickCount
        trackingLinkRepository.save(link);             // Spremi u bazu

        // Ažuriraj lead status
        Lead lead = link.getLead();
        lead.markEngaged();
        leadRepository.save(lead);

        // Preusmeri korisnika na formu
        // return link.getFullUrl();
    }

    // ============================================================
    // PRIMER 5: Pronalaženje Aktivnih Lead-a Kampanje
    // ============================================================
    public java.util.List<Lead> getActiveLead (Long campaignId) {
        return leadRepository.findActiveLessByCampaignId(campaignId);
        // Vraća lead-e koji nisu bounced, blocked ili unsubscribed
    }

    // ============================================================
    // PRIMER 6: Pronalaženje Konvertovanih Lead-a
    // ============================================================
    public java.util.List<Lead> getConvertedLeads(Long campaignId) {
        return leadRepository.findConvertedLeadsByCampaignId(campaignId);
        // Vraća samo lead-e sa statusom CONVERTED
    }

    // ============================================================
    // PRIMER 7: Pronalaženje Kliknutih Link-ova
    // ============================================================
    public java.util.List<TrackingLink> getClickedLinks(Long campaignId) {
        return trackingLinkRepository.findClickedLinksByCampaignId(campaignId);
        // Vraća sve link-ove koji su bar jednom kliknut
    }

    // ============================================================
    // PRIMER 8: Pronalaženje Lead-a po Broju Telefona
    // ============================================================
    public Lead findLeadByPhone(String phoneNumber) {
        return leadRepository.findByPhoneNumber(phoneNumber)
            .orElse(null);
        // Pronađi lead po jedinstvenom broju telefona
    }

    // ============================================================
    // PRIMER 9: Analytics - CTR (Click Through Rate)
    // ============================================================
    public double calculateCTR(Long campaignId) {
        // Ukupan broj link-ova
        java.util.List<TrackingLink> allLinks = trackingLinkRepository.findByCampaignId(campaignId);

        // Ukupan broj klikova
        Long totalClicks = trackingLinkRepository.getTotalClicksByCampaignId(campaignId);

        // CTR = (totalClicks / totalLinks) * 100
        if (allLinks.isEmpty()) {
            return 0.0;
        }
        return (double) totalClicks / allLinks.size() * 100;
    }

    // ============================================================
    // PRIMER 10: Pronalaženje Neukupljenih Lead-a
    // ============================================================
    public java.util.List<Lead> getUnengagedLeads(Long campaignId) {
        return leadRepository.findUnengagedLeadsByCampaignId(campaignId);
        // Vraća lead-e koji nisu imali nikakvu interakciju
    }

    // ============================================================
    // PRIMER 11: Pokretanje Kampanje
    // ============================================================
    public void launchCampaign(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found"));

        // Proveri da li se može pokrenuti
        if (!campaign.canLaunch()) {
            throw new RuntimeException("Campaign cannot be launched in current state: " + campaign.getStatus());
        }

        // Promeni status na ACTIVE
        campaign.setStatus(CampaignStatus.ACTIVE);
        campaign.setStartedAt(LocalDateTime.now());
        campaignRepository.save(campaign);

        // TODO: Pošalji SMS sa tracking link-ovima svim lead-ima
    }

    // ============================================================
    // PRIMER 12: Završavanje Kampanje
    // ============================================================
    public void completeCampaign(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found"));

        campaign.setStatus(CampaignStatus.COMPLETED);
        campaign.setEndedAt(LocalDateTime.now());
        campaignRepository.save(campaign);
    }

    // ============================================================
    // PRIMER 13: Pronalaženje Kampanja po Statusu
    // ============================================================
    public java.util.List<Campaign> getActiveCampaigns(Long adminId) {
        return campaignRepository.findActiveCampaignsByOwnerId(adminId);
        // Vraća sve ACTIVE kampanje određenog admina
    }

    // ============================================================
    // PRIMER 14: Prebrojavanje Lead-a po Statusu
    // ============================================================
    public long countConvertedLeads(Long campaignId) {
        return leadRepository.countByCampaignIdAndStatus(campaignId, LeadStatus.CONVERTED);
        // Broji koliko je lead-a konvertovano
    }

    // ============================================================
    // PRIMER 15: Pronalaženje Tracking Link-a sa FormSubmission
    // ============================================================
    public TrackingLink getTrackingLinkWithSubmission(Long trackingLinkId) {
        return trackingLinkRepository.findByIdWithFormSubmission(trackingLinkId)
            .orElse(null);
        // Pronađi link sa eager loaded FormSubmission
    }
}

// ============================================================
// REST CONTROLLER PRIMERI
// ============================================================

package com.zekiloni.george.web.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignExampleController {

    private final CampaignExampleService service;

    public CampaignExampleController(CampaignExampleService service) {
        this.service = service;
    }

    // Otvaranje tracking link-a (ključan endpoint!)
    @GetMapping("/link/{token}")
    public void trackLinkClick(@PathVariable String token) {
        service.handleLinkClick(token);
        // Preusmeri na formu nakon što se evidentira klik
    }

    // Kreiranje kampanje
    @PostMapping
    public Campaign createCampaign(
        @RequestBody CreateCampaignRequest request
    ) {
        return service.createCampaign(
            request.getAdminId(),
            request.getCampaignName(),
            request.getFormConfigId()
        );
    }

    // Dodavanje lead-a
    @PostMapping("/{campaignId}/leads")
    public Lead addLead(
        @PathVariable Long campaignId,
        @RequestBody AddLeadRequest request
    ) {
        return service.addLeadToCampaign(
            campaignId,
            request.getPhoneNumber(),
            request.getFirstName()
        );
    }

    // Pokretanje kampanje
    @PostMapping("/{campaignId}/launch")
    public void launchCampaign(@PathVariable Long campaignId) {
        service.launchCampaign(campaignId);
    }

    // Analitika
    @GetMapping("/{campaignId}/analytics")
    public AnalyticsResponse getAnalytics(@PathVariable Long campaignId) {
        return new AnalyticsResponse(
            leadRepository.countByCampaignIdAndStatus(campaignId, LeadStatus.NEW),
            leadRepository.countByCampaignIdAndStatus(campaignId, LeadStatus.ENGAGED),
            leadRepository.countByCampaignIdAndStatus(campaignId, LeadStatus.SUBMITTED),
            leadRepository.countByCampaignIdAndStatus(campaignId, LeadStatus.CONVERTED),
            trackingLinkRepository.getTotalClicksByCampaignId(campaignId),
            service.calculateCTR(campaignId)
        );
    }
}

// ============================================================
// DTO PRIMERI (Za Budućnost)
// ============================================================

// CampaignDTO.java
class CampaignDTO {
    private Long id;
    private String name;
    private String description;
    private String status;  // String umesto enum za JSON
    private Long formConfigId;
    private LocalDateTime createdAt;
    // getters, setters...
}

// LeadDTO.java
class LeadDTO {
    private Long id;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastEngagedAt;
    // getters, setters...
}

// TrackingLinkDTO.java
class TrackingLinkDTO {
    private Long id;
    private String token;
    private String shortUrl;
    private Long clickCount;
    private Boolean isClicked;
    private LocalDateTime createdAt;
    private LocalDateTime lastClickedAt;
    // getters, setters...
}

// ============================================================
// SQL QUERY PRIMERI
// ============================================================

/*

-- Pronađi sve kampanje određenog admina
SELECT * FROM campaigns WHERE owner_id = 123;

-- Pronađi sve lead-e kampanje
SELECT * FROM leads WHERE campaign_id = 456;

-- Pronađi lead po broju telefona
SELECT * FROM leads WHERE phone_number = '+381601234567';

-- Pronađi tracking link po tokenu
SELECT * FROM tracking_links WHERE token = 'abc-xyz-123';

-- Pronađi sve kliknut link-ove
SELECT * FROM tracking_links WHERE is_clicked = TRUE;

-- Pronađi top 10 najviše kliknutih link-ova
SELECT * FROM tracking_links
WHERE campaign_id = 456
ORDER BY click_count DESC
LIMIT 10;

-- Izračunaj CTR (Click Through Rate)
SELECT
    COUNT(CASE WHEN is_clicked = TRUE THEN 1 END) * 100.0 / COUNT(*) as ctr
FROM tracking_links
WHERE campaign_id = 456;

-- Pronađi konvertovane lead-e
SELECT * FROM leads WHERE campaign_id = 456 AND status = 'CONVERTED';

-- Pronađi lead-e koji nisu klika
SELECT l.* FROM leads l
LEFT JOIN tracking_links tl ON l.id = tl.lead_id
WHERE l.campaign_id = 456 AND tl.is_clicked = FALSE;

-- Pronađi link sa formom koja je popunjena
SELECT * FROM tracking_links
WHERE campaign_id = 456 AND form_submission_id IS NOT NULL;

*/

// ============================================================
// TESTNI PRIMERI (Unit Tests - Za Budućnost)
// ============================================================

/*

@DataJpaTest
public class CampaignRepositoryTest {

    @Autowired
    private CampaignRepository campaignRepository;

    @Test
    public void shouldFindActiveCampaigns() {
        // Given
        Campaign campaign = Campaign.builder()
            .ownerId(1L)
            .name("Test Campaign")
            .status(CampaignStatus.ACTIVE)
            .build();
        campaignRepository.save(campaign);

        // When
        List<Campaign> result = campaignRepository.findActiveCampaignsByOwnerId(1L);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getStatus()).isEqualTo(CampaignStatus.ACTIVE);
    }
}

@DataJpaTest
public class TrackingLinkRepositoryTest {

    @Autowired
    private TrackingLinkRepository trackingLinkRepository;

    @Test
    public void shouldFindByToken() {
        // Given
        TrackingLink link = TrackingLink.builder()
            .token("unique-token-123")
            .build();
        trackingLinkRepository.save(link);

        // When
        Optional<TrackingLink> result = trackingLinkRepository.findByToken("unique-token-123");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getToken()).isEqualTo("unique-token-123");
    }
}

*/

