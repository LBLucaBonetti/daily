<template>
  <router-view />
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import { api } from './boot/axios';

export default defineComponent({
  name: 'App',

  created: function () {
    window.addEventListener('focus', this.onFocus);
  },
  unmounted: function () {
    window.removeEventListener('focus', this.onFocus);
  },
  methods: {
    onFocus() {
      api.get('/appusers/info').catch((err) => {
        if (err.response?.status === 401) {
          window.location.href = '/';
          return;
        }
      });
    },
  },
});
</script>
