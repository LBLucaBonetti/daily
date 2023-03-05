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
import { useLanguageStore } from 'src/stores/languageStore';
import { PropType, watch } from 'vue';
import { useI18n } from 'vue-i18n';

const props = defineProps({
  localeOptions: { type: Array as PropType<object[]>, required: true },
});

const langList = import.meta.glob(
  '../../node_modules/quasar/lang/(en-US|it).mjs'
);
const { locale } = useI18n({ useScope: 'global' });
const $q = useQuasar();
const language = useLanguageStore();
watch(locale, (val) => {
  langList[`../../node_modules/quasar/lang/${val}.mjs`]().then((lang) => {
    $q.lang.set(lang.default);
    language.setLanguage(locale.value.toString());
  });
});
</script>
