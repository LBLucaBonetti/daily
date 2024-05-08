import { boot } from 'quasar/wrappers';
import { Quasar } from 'quasar';
import { useLanguageStore } from 'src/stores/languageStore';

const langList = import.meta.glob(
  '../../node_modules/quasar/lang/(en-US|it).js',
);

export default boot(async () => {
  const langIso = useLanguageStore().language;

  try {
    langList[`../../node_modules/quasar/lang/${langIso}.js`]().then((lang) => {
      Quasar.lang.set(lang.default);
    });
  } catch (err) {
    // Requested Quasar Language Pack does not exist,
    // let's not break the app, so catching error
  }
});
