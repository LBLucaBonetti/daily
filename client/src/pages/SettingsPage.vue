<template>
  <page-title
    :translatedTitle="$t('pages.settings.title')"
    :translatedSubtitle="$t('pages.settings.subtitle')"
  ></page-title>

  <language-select
    :locale-options="[
      {
        value: 'en-US',
        label: $t('language.english'),
        selected: language.language === 'en-US',
      },
      {
        value: 'it',
        label: $t('language.italian'),
        selected: language.language === 'it',
      },
    ]"
  ></language-select>
</template>
<script setup lang="ts">
import LanguageSelect from 'src/components/LanguageSelect.vue';
import { useLanguageStore } from 'src/stores/languageStore';
import PageTitle from 'src/components/PageTitle.vue';

const language = useLanguageStore();
</script>
