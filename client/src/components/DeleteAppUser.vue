<template>
  <q-btn
    disable
    color="red-8"
    unelevated
    :label="$t('settings.dangerZone.deleteAppUser.button')"
    aria-label="Delete account"
  ></q-btn>
</template>
<script setup lang="ts">
import { QBtn } from 'quasar';
</script>
