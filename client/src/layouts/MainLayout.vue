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

        <q-icon
          tag="span"
          right
          name="img:img/daily-logo.svg"
          size="5rem"
        ></q-icon>
      </q-toolbar>
    </q-header>

    <q-drawer
      class="bg-1"
      v-model="leftDrawerOpen"
      side="left"
      overlay
      bordered
    >
      <q-list class="q-pt-lg">
        <q-avatar
          class="q-mx-md q-mb-md text-2 non-selectable"
          color="primary"
          size="72px"
          >{{ fullNameCapitalizedFirstLetter }}</q-avatar
        >
        <q-item class="justify-between items-center">
          <div>
            <q-item-label class="text-1 text-bold">{{ fullName }}</q-item-label>
            <q-item-label class="text-1">{{ email }}</q-item-label>
          </div>
          <div>
            <logout-button />
          </div>
        </q-item>
      </q-list>
      <q-toolbar class="q-pt-lg">
        <q-tabs
          vertical
          indicator-color="primary"
          switch-indicator
          class="text-1 col-grow"
          active-color="primary"
          inline-label
        >
          <page-selector
            to="/notes"
            svg-id="notes"
            :translated-label="$t('pages.notes.title')"
          ></page-selector>
          <page-selector
            to="/tags"
            svg-id="tags"
            :translated-label="$t('pages.tags.title')"
          ></page-selector>

          <page-selector
            to="/settings"
            svg-id="settings"
            :translated-label="$t('pages.settings.title')"
          ></page-selector>
        </q-tabs>
      </q-toolbar>
    </q-drawer>

    <q-page-container>
      <q-page class="text-1 w-sm-50" padding>
        <router-view />
      </q-page>
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { heroOutline24Bars3 } from 'quasar-extras-svg-icons/hero-icons-v2';
import { api } from 'src/boot/axios';
import { AxiosResponse } from 'axios';
import LogoutButton from 'components/LogoutButton.vue';
import PageSelector from 'src/components/PageSelector.vue';
import InfoDto from 'src/interfaces/InfoDto';
import { refreshPage } from 'src/utils/refresh-page';
import {
  QLayout,
  QHeader,
  QToolbar,
  QBtn,
  QDrawer,
  QList,
  QItemLabel,
  QItem,
  QPageContainer,
  QPage,
} from 'quasar';
import { isAxios401 } from 'src/utils/is-axios-401';
import { RouterView } from 'vue-router';

const leftDrawerOpen = ref(false);

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value;
}

const fullName = ref('');
const email = ref('');
const fullNameCapitalizedFirstLetter = ref('');

onMounted(() => {
  // Get info
  api
    .get('/appusers/info')
    .then((res: AxiosResponse<InfoDto>) => {
      fullName.value = res.data.fullName;
      fullNameCapitalizedFirstLetter.value = fullName.value
        ? fullName.value.charAt(0).toUpperCase()
        : '?';
      email.value = res.data.email;
    })
    .catch((err) => {
      if (isAxios401(err)) {
        refreshPage();
      }
    });
});
</script>
