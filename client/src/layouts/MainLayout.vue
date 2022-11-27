<template>
  <q-layout view="hHh Lpr lfr">
    <q-header bordered class="bg-1">
      <q-toolbar>
        <q-btn
          flat
          dense
          round
          :icon="heroOutline24Bars3"
          aria-label="Menu"
          class="text-1"
          @click="toggleLeftDrawer"
        />

        <q-toolbar-title class="poppins-regular text-1">
          daily
        </q-toolbar-title>
      </q-toolbar>
    </q-header>

    <q-drawer
      class="bg-1"
      v-model="leftDrawerOpen"
      side="left"
      overlay
      bordered
      behavior="mobile"
    >
      <q-list>
        <q-item-label class="poppins-regular" header> daily </q-item-label>
        <q-item class="justify-between items-center">
          <div>
            <q-item-label class="inter-regular text-1">{{
              fullName
            }}</q-item-label>
            <q-item-label class="inter-regular text-1">{{
              email
            }}</q-item-label>
          </div>
          <div>
            <logout-button />
          </div>
        </q-item>
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { heroOutline24Bars3 } from 'quasar-extras-svg-icons/hero-icons-v2';
import { api } from 'src/boot/axios';
import { AxiosResponse } from 'axios';
import LogoutButton from 'components/LogoutButton.vue';

const leftDrawerOpen = ref(false);

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value;
}

const fullName = ref('');
const email = ref('');

interface Info {
  fullName: string;
  email: string;
}
onMounted(() => {
  api
    .get('/appusers/info')
    .then((res: AxiosResponse<Info>) => {
      fullName.value = res.data.fullName;
      email.value = res.data.email;
    })
    .catch((err) => {
      if (err.response?.status === 401) {
        window.location.href = window.location.href;
        return;
      }
    });
});
</script>
