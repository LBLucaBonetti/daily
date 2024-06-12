<template>
  <h1 class="text-left text-h3 text-sm-h2 non-selectable q-my-md">
    {{ translatedTitle }}
  </h1>
  <p class="text-subtitle1 q-mb-xl">{{ translatedSubtitle }}</p>
</template>
<script setup lang="ts">
defineProps({
  translatedTitle: { type: String, required: true },
  translatedSubtitle: { type: String, required: true },
});
</script>
