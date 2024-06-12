<template>
  <q-btn
    flat
    dense
    :ripple="false"
    @click="logout()"
    :icon="heroOutline24ArrowRightOnRectangle"
    class="text-1"
  ></q-btn>
  <form hidden :id="logoutFormId" method="post" action="/logout"></form>
</template>

<script setup lang="ts">
import { heroOutline24ArrowRightOnRectangle } from 'quasar-extras-svg-icons/hero-icons-v2';
import { Cookies } from 'quasar';

const logoutFormId = 'logout-form';
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
