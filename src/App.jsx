import { useEffect, useMemo, useRef, useState } from 'react';
import { LANGUAGES, SCHEMES, buildResponse, findSchemeMatches, getCopy } from './sakhiData';

const STORAGE_KEY = 'sakhi-saved-schemes-v1';

function loadSaved() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    const parsed = JSON.parse(raw ?? '[]');
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
}

function App() {
  const [language, setLanguage] = useState(LANGUAGES[0]);
  const [query, setQuery] = useState('');
  const [answer, setAnswer] = useState('');
  const [listening, setListening] = useState(false);
  const [speechSupported, setSpeechSupported] = useState(true);
  const [saved, setSaved] = useState(loadSaved);
  const [privacyMode, setPrivacyMode] = useState(true);
  const recognitionRef = useRef(null);

  const copy = useMemo(() => getCopy(language[2]), [language]);
  const matches = useMemo(() => findSchemeMatches(query), [query]);

  useEffect(() => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(saved));
  }, [saved]);

  useEffect(() => {
    if (typeof window === 'undefined') return;
    const Recognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    setSpeechSupported(Boolean(Recognition));
    if (!Recognition) return;

    const recognition = new Recognition();
    recognition.continuous = false;
    recognition.interimResults = true;
    recognition.lang = language[1];

    recognition.onstart = () => setListening(true);
    recognition.onend = () => setListening(false);
    recognition.onerror = () => setListening(false);
    recognition.onresult = (event) => {
      const transcript = Array.from(event.results)
        .map((result) => result[0]?.transcript ?? '')
        .join(' ')
        .trim();
      setQuery(transcript);
      const finalResult = event.results[event.results.length - 1]?.isFinal;
      if (finalResult && transcript) setAnswer(buildResponse(transcript, language[2]));
    };

    recognitionRef.current = recognition;
    return () => {
      recognition.stop?.();
      recognitionRef.current = null;
    };
  }, [language]);

  function toggleListening() {
    const recognition = recognitionRef.current;
    if (!recognition) {
      setAnswer('Voice input is not available in this browser. Please use the text box or try a Chromium-based browser with microphone permission enabled.');
      return;
    }
    try {
      if (listening) recognition.stop();
      else recognition.start();
    } catch {
      setListening(false);
    }
  }

  function askSakhi() {
    const text = query.trim();
    if (!text) {
      setAnswer('Please type a question or use the microphone button.');
      return;
    }
    const response = buildResponse(text, language[2]);
    setAnswer(response);
    if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel();
      const utterance = new SpeechSynthesisUtterance(response);
      utterance.lang = language[1];
      utterance.rate = 0.95;
      window.speechSynthesis.speak(utterance);
    }
  }

  function toggleSaved(id) {
    setSaved((current) => current.includes(id) ? current.filter((item) => item !== id) : [...current, id]);
  }

  const visibleQuery = privacyMode ? query : query;

  return (
    <div className="page-shell">
      <header className="topbar">
        <div className="container nav-wrap">
          <a className="brand" href="#top" aria-label="SakhiVoice AI home">
            <div className="brand-mark" aria-hidden="true">S</div>
            <span>SakhiVoice AI</span>
          </a>
          <nav className="nav" aria-label="Main navigation">
            <a href="#voice">Voice assistant</a>
            <a href="#schemes">Schemes</a>
            <a href="#safety">Safety</a>
          </nav>
          <button className="nav-cta" onClick={() => document.getElementById('voice')?.scrollIntoView({ behavior: 'smooth' })}>Try demo</button>
        </div>
      </header>

      <main id="top">
        <section className="hero">
          <div className="container hero-grid">
            <div className="hero-copy">
              <span className="eyebrow">Voice-first public-service discovery</span>
              <h1>Government support, explained in a language that feels familiar.</h1>
              <p className="lede">Ask by voice or text. SakhiVoice AI matches your request to a small offline demo catalog, explains the next step, and sends you to an official portal for verification.</p>

              <div className="language-picker" aria-label="Language selector">
                {LANGUAGES.map((item) => (
                  <button key={item[1]} type="button" className={`language-btn ${language[1] === item[1] ? 'active' : ''}`} onClick={() => setLanguage(item)} aria-pressed={language[1] === item[1]}>
                    {item[0]}
                  </button>
                ))}
              </div>

              <div className="hero-actions">
                <button className="primary-btn" onClick={() => document.getElementById('voice')?.scrollIntoView({ behavior: 'smooth' })}>Speak with Sakhi</button>
                <button className="secondary-btn" onClick={() => document.getElementById('schemes')?.scrollIntoView({ behavior: 'smooth' })}>Explore schemes</button>
              </div>

              <div className="trust-strip" aria-label="Product capabilities">
                <span>12 UI languages</span><span>Voice + text fallback</span><span>No sensitive data stored</span>
              </div>
            </div>

            <div className="assistant-card" id="voice">
              <div className="assistant-header">
                <div className={`status ${listening ? 'live' : ''}`} aria-live="polite">
                  <span className="dot" aria-hidden="true"></span>
                  <span>{listening ? 'Listening' : 'Ready'}</span>
                </div>
                <label className="privacy-toggle">
                  <input type="checkbox" checked={privacyMode} onChange={(event) => setPrivacyMode(event.target.checked)} />
                  <span>Privacy mode</span>
                </label>
              </div>

              <div className="assistant-avatar" aria-hidden="true">
                <div className="ring ring-one"></div><div className="ring ring-two"></div>
                <div className="avatar-core"><span>S</span></div>
              </div>

              <div className="voice-wave" aria-hidden="true">{[1, 2, 3, 4, 5, 6, 7].map((item) => <span key={item} className={listening ? 'pulse' : ''}></span>)}</div>

              <div className="prompt-card" aria-live="polite">
                <p className="prompt-label">Sakhi says</p>
                <p className="prompt-text">{answer || copy.greeting}</p>
              </div>

              <div className="query-row">
                <label className="sr-only" htmlFor="sakhi-query">Ask SakhiVoice</label>
                <textarea id="sakhi-query" rows="3" value={visibleQuery} onChange={(event) => setQuery(event.target.value)} placeholder={copy.placeholder} />
                <div className="query-actions">
                  <button type="button" className={`listen-btn ${listening ? 'is-active' : ''}`} onClick={toggleListening} aria-pressed={listening}>
                    <span aria-hidden="true">🎙️</span><span>{listening ? 'Stop' : 'Voice'}</span>
                  </button>
                  <button type="button" className="primary-btn compact" onClick={askSakhi}>Ask Sakhi</button>
                </div>
              </div>

              {!speechSupported && <p className="support-note">Voice recognition is unavailable here. The text mode is fully usable without it.</p>}
            </div>
          </div>
        </section>

        <section className="feature-strip">
          <div className="container strip-grid">
            <article className="feature-card"><div className="icon-wrap rose" aria-hidden="true">🗣️</div><h3>Natural input</h3><p>Voice recognition when the browser supports it, with an always-available text fallback.</p></article>
            <article className="feature-card"><div className="icon-wrap gold" aria-hidden="true">🧭</div><h3>Explain, don’t overwhelm</h3><p>Responses are short, practical, and point users back to official sources for verification.</p></article>
            <article className="feature-card"><div className="icon-wrap teal" aria-hidden="true">🔒</div><h3>Privacy by default</h3><p>Only saved scheme IDs are kept locally. No audio recording or transcript database is created by this demo.</p></article>
          </div>
        </section>

        <section className="section" id="schemes">
          <div className="container">
            <div className="section-heading"><span className="eyebrow">Scheme discovery</span><h2>Search a demo catalog and open the official source.</h2></div>
            <div className="scheme-search"><label htmlFor="scheme-search">Search schemes</label><input id="scheme-search" value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Try education, maternity, business, LPG…" /></div>
            <div className="scheme-grid">
              {(matches.length ? matches : SCHEMES).map((scheme) => (
                <article className="scheme-card" key={scheme.id}>
                  <div className="scheme-top"><span className="tag">{scheme.category}</span><button className="save-btn" onClick={() => toggleSaved(scheme.id)} aria-label={`${saved.includes(scheme.id) ? 'Unsave' : 'Save'} ${scheme.title}`}>{saved.includes(scheme.id) ? '★' : '☆'}</button></div>
                  <h3>{scheme.title}</h3><p>{scheme.description}</p>
                  <div className="scheme-actions"><a className="text-link" href={scheme.official} target="_blank" rel="noreferrer noopener">Official source ↗</a><button className="secondary-btn small" onClick={() => { setQuery(scheme.keywords[0]); setAnswer(buildResponse(scheme.title, language[2])); }}>Explain</button></div>
                </article>
              ))}
            </div>
            <p className="source-note">Demo catalog only: eligibility, benefit amounts, dates, and application procedures can change. Always verify on the official portal before applying.</p>
          </div>
        </section>

        <section className="section alt" id="safety">
          <div className="container safety-grid">
            <div className="safety-copy"><span className="eyebrow">Safety & accessibility</span><h2>Designed for real-world use, not just a demo screen.</h2><p>Keyboard-friendly controls, visible focus states, live status updates, text fallback, and privacy-first local storage make the experience more resilient.</p><div className="check-list"><div><span>✓</span> No secret keys in the frontend</div><div><span>✓</span> External links use safer opener settings</div><div><span>✓</span> Saved state stays in the browser</div></div></div>
            <div className="discreet-panel"><div className="mini-topbar"><span className="mini-dot"></span><span>Saved schemes</span></div><div className="saved-list">{saved.length === 0 ? <p>No schemes saved yet.</p> : saved.map((id) => { const scheme = SCHEMES.find((item) => item.id === id); return <div className="saved-item" key={id}><span>{scheme?.title}</span><button onClick={() => toggleSaved(id)} aria-label={`Remove ${scheme?.title}`}>Remove</button></div>; })}</div></div>
          </div>
        </section>
      </main>

      <footer className="site-footer"><div className="container footer-wrap"><div><div className="brand"><div className="brand-mark" aria-hidden="true">S</div><span>SakhiVoice AI</span></div><p>Prototype architecture focused on voice accessibility, safe guidance, and verifiable public-service discovery.</p></div><button className="primary-btn" onClick={() => window.scrollTo({ top: 0, behavior: 'smooth' })}>Back to top</button></div></footer>
    </div>
  );
}

export default App;
