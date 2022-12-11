<template>
  <router-view />
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import { api } from './boot/axios';
import { isAxios401 } from './utils/is-axios-401';
import { refreshPage } from './utils/refresh-page';

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
        if (isAxios401(err)) {
          refreshPage();
        }
      });
    },
  },
});
</script>
