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
import { PropType } from 'vue';
import { useI18n } from 'vue-i18n';

const props = defineProps({
  localeOptions: { type: Array as PropType<object[]>, required: true },
});

const { locale } = useI18n({ useScope: 'global' });
</script>
