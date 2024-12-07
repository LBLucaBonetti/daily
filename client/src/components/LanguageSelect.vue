<template>
  <q-select
    v-model="locale"
    :options="props.localeOptions"
    :label="$t('language.label')"
    stack-label
    outlined
    emit-value
    map-options
    style="min-width: 150px"
  />
</template>

<script setup lang="ts">
import { useQuasar } from 'quasar';
import type LocaleOptions from 'src/interfaces/LocaleOptions';
import { useLanguageStore } from 'src/stores/languageStore';
import { type PropType, onMounted, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { api } from 'src/boot/axios';
import type SettingsDto from 'src/interfaces/SettingsDto';
import { isAxios401 } from 'src/utils/is-axios-401';
import { refreshPage } from 'src/utils/refresh-page';

const props = defineProps({
  localeOptions: { type: Array as PropType<LocaleOptions[]>, required: true },
});

const langList = import.meta.glob(
  '../../node_modules/quasar/lang/(en-US|it).js',
);
const { locale } = useI18n({ useScope: 'global' });
const $q = useQuasar();
const language = useLanguageStore();

watch(locale, (val) => {
  updateLang(val);
});
onMounted(() => {
  const selectedLang = props.localeOptions.find((lo) => lo.selected === true);
  if (selectedLang) {
    updateLang(selectedLang.value);
  }
});

function updateLang(val: object | string) {
  langList[`../../node_modules/quasar/lang/${val}.js`]().then((lang) => {
    $q.lang.set(lang.default);
    const langIso = locale.value.toString();
    language.setLanguage(langIso);
    const updateSettingsDto: SettingsDto = {
      lang: langIso,
    };
    try {
      api.put('/appusers/settings', updateSettingsDto);
    } catch (err) {
      if (isAxios401(err)) {
        refreshPage();
      }
    }
  });
}
</script>
