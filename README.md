# SakhiVoice AI

SakhiVoice AI is a frontend-first prototype for multilingual public-service discovery.

## What is implemented

- React + Vite application with a single entry point
- Browser SpeechRecognition / webkitSpeechRecognition when supported
- SpeechSynthesis responses when available
- 12 Indian-language UI options with localized voice locales
- Text fallback for browsers without speech recognition
- Offline keyword-based scheme matching for demo use
- Official-source links for the demo scheme catalog
- Local-only saved scheme IDs using `localStorage`
- Privacy mode UX and no hard-coded API keys
- Accessible labels, live regions, focus states and reduced-motion support
- Node built-in tests (`npm test`) for the matching layer

## Local setup

```bash
npm install
npm run dev
npm test
npm run build
```

## Important demo note

This project intentionally does not collect sensitive personal information or send voice data to a backend. The matching engine is a deterministic demo layer. Government-scheme eligibility, benefit values, deadlines and application rules can change, so users should verify the latest details on the linked official portal before applying.
