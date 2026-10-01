export const LANGUAGES = [
  ['English', 'en-IN', 'English'],
  ['हिन्दी', 'hi-IN', 'Hindi'],
  ['தமிழ்', 'ta-IN', 'Tamil'],
  ['తెలుగు', 'te-IN', 'Telugu'],
  ['ಕನ್ನಡ', 'kn-IN', 'Kannada'],
  ['മലയാളം', 'ml-IN', 'Malayalam'],
  ['বাংলা', 'bn-IN', 'Bengali'],
  ['मराठी', 'mr-IN', 'Marathi'],
  ['ગુજરાતી', 'gu-IN', 'Gujarati'],
  ['ਪੰਜਾਬੀ', 'pa-IN', 'Punjabi'],
  ['ଓଡ଼ିଆ', 'or-IN', 'Odia'],
  ['অসমীয়া', 'as-IN', 'Assamese'],
];

export const LANGUAGE_COPY = {
  English: {
    greeting: 'Hello! Tell me what support you need, and I will guide you step by step.',
    listening: 'Listening… speak naturally.',
    placeholder: 'Try: scholarship, LPG connection, maternity support…',
    responsePrefix: 'Here is a starting point:',
  },
  Hindi: {
    greeting: 'नमस्ते! आपको किस मदद की जरूरत है, बताइए। मैं आसान तरीके से मार्गदर्शन दूँगा।',
    listening: 'सुन रहा हूँ… आराम से बोलिए।',
    placeholder: 'जैसे: छात्रवृत्ति, गैस कनेक्शन, मातृत्व सहायता…',
    responsePrefix: 'यह एक उपयोगी शुरुआत हो सकती है:',
  },
  Tamil: {
    greeting: 'வணக்கம்! உங்களுக்கு தேவையான உதவியை சொல்லுங்கள். படிப்படியாக வழிகாட்டுகிறேன்.',
    listening: 'கேட்கிறேன்… இயல்பாக பேசுங்கள்.',
    placeholder: 'உதாரணம்: கல்வி உதவித்தொகை, எரிவாயு இணைப்பு…',
    responsePrefix: 'இதிலிருந்து தொடங்கலாம்:',
  },
};

const DEFAULT_COPY = {
  greeting: 'Hello! Tell me what support you need, and I will guide you step by step.',
  listening: 'Listening… speak naturally.',
  placeholder: 'Tell me what help you need…',
  responsePrefix: 'Here is a starting point:',
};

export function getCopy(languageName) {
  return LANGUAGE_COPY[languageName] ?? DEFAULT_COPY;
}

export const SCHEMES = [
  {
    id: 'ujjwala',
    title: 'PM Ujjwala Yojana',
    category: 'Livelihood & Household',
    description: 'Demo guidance for LPG-connection support. Confirm eligibility and current application rules on the official portal.',
    keywords: ['lpg', 'gas', 'ujjwala', 'cooking', 'cylinder', 'எரிவாயு', 'गैस'],
    official: 'https://www.pmuy.gov.in/',
  },
  {
    id: 'matru',
    title: 'Pradhan Mantri Matru Vandana Yojana',
    category: 'Maternity & Health',
    description: 'Demo guidance for maternity benefit information. Current eligibility and benefit rules should be verified officially.',
    keywords: ['maternity', 'pregnancy', 'pregnant', 'mother', 'matru', 'கர்ப்பம்', 'मातृत्व'],
    official: 'https://pmmvy.wcd.gov.in/',
  },
  {
    id: 'scholarship',
    title: 'National Scholarship Portal',
    category: 'Education',
    description: 'Explore scholarship discovery and application information. Requirements vary by scheme, course and applicant.',
    keywords: ['scholarship', 'study', 'college', 'education', 'student', 'fees', 'education support', 'உதவித்தொகை', 'छात्रवृत्ति'],
    official: 'https://scholarships.gov.in/',
  },
  {
    id: 'mudra',
    title: 'Pradhan Mantri MUDRA Yojana',
    category: 'Entrepreneurship',
    description: 'Business-loan information for eligible micro and small enterprise needs. Check current lender terms before applying.',
    keywords: ['business', 'shop', 'startup', 'loan', 'mudra', 'small business', 'व्यवसाय', 'கடன்'],
    official: 'https://www.mudra.org.in/',
  },
];

export function normalizeText(input = '') {
  return input
    .toLocaleLowerCase('en-IN')
    .normalize('NFKC')
    .replace(/[.,!?;:()[\]{}]/g, ' ')
    .replace(/\s+/g, ' ')
    .trim();
}

export function findSchemeMatches(query, schemes = SCHEMES) {
  const text = normalizeText(query);
  if (!text) return [];

  return schemes
    .map((scheme) => {
      const score = scheme.keywords.reduce((total, keyword) => {
        return total + (text.includes(normalizeText(keyword)) ? 1 : 0);
      }, 0);
      return { scheme, score };
    })
    .filter(({ score }) => score > 0)
    .sort((a, b) => b.score - a.score || a.scheme.title.localeCompare(b.scheme.title))
    .map(({ scheme }) => scheme);
}

export function buildResponse(query, languageName) {
  const copy = getCopy(languageName);
  const matches = findSchemeMatches(query);
  if (matches.length === 0) {
    return `${copy.responsePrefix} I can search by topics such as education, maternity, LPG support, or small-business help. For accurate eligibility, please verify details on the official scheme website.`;
  }
  const names = matches.slice(0, 2).map((item) => item.title).join(' and ');
  return `${copy.responsePrefix} ${names}. I can explain the next steps and the documents to check. Please confirm the latest eligibility and application details on the official portal.`;
}
