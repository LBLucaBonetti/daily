<template>
  <div class="fullscreen bg-1 text-1 text-center q-pa-md flex flex-center">
    <div>
      <div style="font-size: 30vh">404</div>

      <div class="text-h2" style="opacity: 0.4">
        {{ $t('pages.notFound.text') }}
      </div>

      <q-btn
        unelevated
        :label="$t('pages.notFound.button')"
        aria-label="Go home"
        class="q-mt-xl not-found-back-home-btn"
        color="primary"
        to="/"
      />
    </div>
  </div>
</template>

<script setup lang="ts"></script>
<style>
.not-found-back-home-btn {
  min-width: 300px;
}
</style>
