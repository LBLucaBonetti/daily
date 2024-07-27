<template>
  <page-title
    :translatedTitle="$t('pages.settings.title')"
    :translatedSubtitle="$t('pages.settings.subtitle')"
  ></page-title>

  <section>
    <language-select
      :locale-options="[
        {
          value: 'en-US',
          label: $t('language.english'),
          selected: language.language === 'en-US',
        },
        {
          value: 'it',
          label: $t('language.italian'),
          selected: language.language === 'it',
        },
      ]"
    ></language-select>
  </section>

  <q-separator spaced="xl"></q-separator>

  <section>
    <q-card flat bordered class="bg-red-1 text-red-10"
      ><q-card-section
        ><div class="text-h6">
          {{ $t('settings.dangerZone.title') }}
        </div>
        <div class="text-subtitle-2">
          {{ $t('settings.dangerZone.subtitle') }}
        </div></q-card-section
      >
      <q-card-section
        ><q-card-actions class="no-padding" :vertical="$q.screen.lt.sm">
          <password-change v-if="isDailyAppUser"></password-change>
          <delete-app-user></delete-app-user></q-card-actions></q-card-section
    ></q-card>
  </section>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { api } from 'src/boot/axios';
import { AxiosResponse } from 'axios';
import LanguageSelect from 'src/components/LanguageSelect.vue';
import { useLanguageStore } from 'src/stores/languageStore';
import PageTitle from 'src/components/PageTitle.vue';
import InfoDto from 'src/interfaces/InfoDto';
import { isAxios401 } from 'src/utils/is-axios-401';
import { refreshPage } from 'src/utils/refresh-page';
import { isDailyAuthProvider } from 'src/interfaces/AuthProvider';
import PasswordChange from 'src/components/PasswordChange.vue';
import { QSeparator, QCard, QCardSection, QCardActions } from 'quasar';
import DeleteAppUser from 'src/components/DeleteAppUser.vue';

const language = useLanguageStore();
const isDailyAppUser = ref(false);
onMounted(() => {
  // Get info
  api
    .get('/appusers/info')
    .then((res: AxiosResponse<InfoDto>) => {
      isDailyAppUser.value = isDailyAuthProvider(res.data.authProvider);
    })
    .catch((err) => {
      if (isAxios401(err)) {
        refreshPage();
      }
    });
});
</script>
