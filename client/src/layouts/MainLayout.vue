<template>
  <q-layout view="hHh Lpr lfr" class="bg-1">
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

        <q-icon tag="span" right name="img:img/logo.svg" size="2rem"></q-icon>
        <q-toolbar-title class="poppins-regular text-1 non-selectable">
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
        <q-item
          ><language-select
            :locale-options="[
              { value: 'en-US', label: $t('language.english') },
              { value: 'it', label: $t('language.italian') },
            ]"
          ></language-select
        ></q-item>
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
import LanguageSelect from 'src/components/LanguageSelect.vue';
import InfoDto from 'src/interfaces/InfoDto';
import { refreshPage } from 'src/utils/refresh-page';
import {
  QLayout,
  QHeader,
  QToolbar,
  QBtn,
  QToolbarTitle,
  QDrawer,
  QList,
  QItemLabel,
  QItem,
  QPageContainer,
} from 'quasar';
import { isAxios401 } from 'src/utils/is-axios-401';
import { RouterView } from 'vue-router';

const leftDrawerOpen = ref(false);

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value;
}

const fullName = ref('');
const email = ref('');

onMounted(() => {
  api
    .get('/appusers/info')
    .then((res: AxiosResponse<InfoDto>) => {
      fullName.value = res.data.fullName;
      email.value = res.data.email;
    })
    .catch((err) => {
      if (isAxios401(err)) {
        refreshPage();
      }
    });
});
</script>
