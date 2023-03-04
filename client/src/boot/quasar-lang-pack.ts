import { Quasar } from 'quasar';

const langList = import.meta.glob(
  '../../node_modules/quasar/lang/(en-US|it).mjs'
);

export default async () => {
  const langIso = 'en-US'; // ... some logic to determine it (use Cookies Plugin?)

  try {
    langList[`../../node_modules/quasar/lang/${langIso}.mjs`]().then((lang) => {
      Quasar.lang.set(lang.default);
    });
  } catch (err) {
    // Requested Quasar Language Pack does not exist,
    // let's not break the app, so catching error
  }
};
