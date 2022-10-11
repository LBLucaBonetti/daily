<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn
          flat
          dense
          round
          :icon="heroOutline24Bars3"
          aria-label="Menu"
          @click="toggleLeftDrawer"
        />

        <q-toolbar-title class="poppins-regular"> daily </q-toolbar-title>
      </q-toolbar>
    </q-header>

    <q-drawer v-model="leftDrawerOpen" show-if-above bordered>
      <q-list>
        <q-item-label class="poppins-regular" header> daily </q-item-label>
        <q-item
          ><q-btn
            flat
            dense
            :ripple="false"
            @click="logout()"
            :icon="heroOutline24ArrowRightOnRectangle"
          ></q-btn>
          <form hidden :id="logoutFormId" method="post" action="/logout"></form
        ></q-item>
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import {
  heroOutline24Bars3,
  heroOutline24ArrowRightOnRectangle,
} from 'quasar-extras-svg-icons/hero-icons-v2';
import { Cookies } from 'quasar';

const leftDrawerOpen = ref(false);
const logoutFormId = 'logout-form';

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value;
}

function logout() {
  // Get the hidden form
  const form = document.getElementById(logoutFormId) as HTMLFormElement;
  // Add the CSRF token
  const csrf = document.createElement('input');
  csrf.setAttribute('id', 'csrf-token');
  csrf.setAttribute('type', 'hidden');
  csrf.setAttribute('name', '_csrf');
  // Append it to the form
  form.appendChild(csrf);

  const cookieCsrf = Cookies.get('XSRF-TOKEN');
  if (cookieCsrf) {
    csrf.setAttribute('value', cookieCsrf);
  }
  // Submit the form, effectively logout
  form.submit();
}
</script>
