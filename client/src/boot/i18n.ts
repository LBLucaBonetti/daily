import { boot } from 'quasar/wrappers';
import { createI18n } from 'vue-i18n';
import { useLanguageStore } from 'src/stores/languageStore';
import messages from 'src/i18n';

export default boot(async ({ app, store }) => {
  const response = await fetch('/api/appusers/settings');
  const settings = await response.json();
  const langIso = settings.lang ?? 'en-US';
  useLanguageStore().setLanguage(langIso);

  const i18n = createI18n({
    locale: langIso,
    globalInjection: true,
    messages,
  });

  // Set i18n instance on app
  app.use(i18n);
});
