<template>
  <q-select
    v-model="locale"
    :options="props.localeOptions"
    :label="$t('language.label')"
    dense
    borderless
    emit-value
    map-options
    options-dense
    style="min-width: 150px"
  />
</template>

<script setup lang="ts">
import { useQuasar } from 'quasar';
import LocaleOptions from 'src/interfaces/LocaleOptions';
import { useLanguageStore } from 'src/stores/languageStore';
import { PropType, onMounted, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { api } from 'src/boot/axios';
import SettingsDto from 'src/interfaces/SettingsDto';
import { isAxios401 } from 'src/utils/is-axios-401';
import { refreshPage } from 'src/utils/refresh-page';

const props = defineProps({
  localeOptions: { type: Array as PropType<LocaleOptions[]>, required: true },
});

const langList = import.meta.glob(
  '../../node_modules/quasar/lang/(en-US|it).mjs'
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
  langList[`../../node_modules/quasar/lang/${val}.mjs`]().then((lang) => {
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
