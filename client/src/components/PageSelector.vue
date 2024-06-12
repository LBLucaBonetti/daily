<template>
  <q-route-tab :to="to" :exact="false" style="justify-content: flex-start">
    <q-icon :name="iconName" left size="24px" />
    {{ translatedLabel }}
  </q-route-tab>
</template>
<script setup lang="ts">
import { QRouteTab, QIcon } from 'quasar';
import { onMounted, ref } from 'vue';

const iconName = ref('');

const props = defineProps({
  to: { type: String, required: true },
  svgId: { type: String, required: true },
  translatedLabel: { type: String, required: true },
});

onMounted(() => {
  iconName.value = `svguse:icons/svg-defs.svg#${props.svgId}|0 0 256 256`;
});
</script>
