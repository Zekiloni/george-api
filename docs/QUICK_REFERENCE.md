com.zekiloni.george
├── application          ← use cases, services, orchestrators
│   ├── campaign
│   ├── form
│   ├── gsm
│   ├── link
│   ├── lead
│   └── revenue
│
├── domain
│   ├── campaign
│   │   ├── Campaign.java
│   │   ├── CampaignStatus.java
│   │   ├── CampaignType.java
│   │   └── CampaignMessage.java
│   │
│   ├── lead
│   │   ├── Lead.java
│   │   ├── LeadStatus.java
│   │   └── LeadImport.java
│   │
│   ├── link
│   │   ├── TrackingLink.java          ← najvažnije
│   │   ├── LinkClick.java
│   │   └── LinkTokenGenerator.java
│   │
│   ├── form
│   │   ├── FormConfig.java            ← već imaš
│   │   ├── FormField.java
│   │   ├── FormSubmission.java        ← već imaš
│   │   ├── SubmissionAnswer.java
│   │   └── ValidationType.java
│   │
│   ├── gsm
│   │   ├── ... (ovo ti je već dobro)
│   │
│   ├── result
│   │   ├── CampaignResult.java
│   │   └── RevenueShareLog.java       ← za tvoj 10%
│   │
│   └── user
│       ├── PlatformUser.java
│       └── Subscription.java
│
├── infrastructure
│   ├── persistence      ← repositories + JPA config
│   ├── gsm              ← konkretni GSM provideri
│   ├── messaging        ← SMS queue, MessageBank
│   └── payment          ← za naplatu i revenue share
│
├── web
│   ├── controller
│   ├── dto
│   └── security
│
└── config