import { Lang } from 'quasar'
import { useLanguageStore } from 'src/stores/languageStore';

const langList = import.meta.glob(
  '../../node_modules/quasar/lang/(en-US|it).js',
);

export default async () => {
  const langIso = useLanguageStore().language;

  try {
    const effectiveLang = langList[ `../../node_modules/quasar/lang/${ langIso }.js` ];
    if(!effectiveLang) {
      console.warn("Could not find language; a default one will be used instead");
      return;
    }
    effectiveLang().then((lang: any) => {
      Lang.set(lang.default)
    })
  }
  catch (err) {
    console.error(err)
    // Requested Quasar Language Pack does not exist,
    // let's not break the app, so catching error
    console.debug(err);
    console.warn("Could not find language; a default one will be used instead")
  }
}
