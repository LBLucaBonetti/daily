<template>
  <page-title
    :translatedTitle="$t('pages.budget.title')"
    :translatedSubtitle="$t('pages.budget.subtitle')"
  ></page-title>

  <q-infinite-scroll @load="onLoad" ref="infiniteScroll">
    <money-card
      v-for="money in moneys"
      :key="money.uuid"
      @reload-money="reloadMoney"
      :money="money"
    />

    <template v-slot:loading>
      <q-card class="q-mt-md bg-1 text-1" flat bordered>
        <q-card-section class="q-pa-sm">
          <q-skeleton type="text" class="money-skeleton-text"></q-skeleton>
        </q-card-section>

        <q-card-section class="q-pa-sm" horizontal>
          <q-skeleton
            type="text"
            class="q-mr-md"
            style="min-width: 2rem; border-radius: 4px"
          ></q-skeleton>
          <q-skeleton
            type="text"
            class="q-mx-md"
            style="min-width: 1.5rem; border-radius: 4px"
          ></q-skeleton>
          <q-skeleton
            type="text"
            class="q-ml-md"
            style="min-width: 3rem; border-radius: 4px"
          ></q-skeleton>
        </q-card-section>

        <q-card-actions class="q-pa-sm">
          <q-skeleton
            type="text"
            class="money-skeleton-text"
            style="min-width: 8rem"
          ></q-skeleton>
          <q-space></q-space>
          <q-skeleton type="QBtn"></q-skeleton>
          <q-skeleton type="QBtn" class="q-ml-sm"></q-skeleton>
        </q-card-actions>
      </q-card>
    </template>
  </q-infinite-scroll>
</template>

<script setup lang="ts">
import PageTitle from 'src/components/PageTitle.vue';
import MoneyCard from 'src/components/MoneyCard.vue';
import { api } from 'boot/axios';
import type { AxiosError, AxiosResponse } from 'axios';
import type PageDto from 'src/interfaces/PageDto';
import type MoneyDto from 'src/interfaces/MoneyDto';
import { isAxios401 } from 'src/utils/is-axios-401';
import { refreshPage } from 'src/utils/refresh-page';
import { notifyPosition } from 'src/utils/notify-position';
import { useI18n } from 'vue-i18n';
import { QInfiniteScroll, QSkeleton, useQuasar } from 'quasar';
import { ref } from 'vue';
import { useMoneyInEditStateStore } from 'stores/moneyEditingStore';

const { t } = useI18n();
const $q = useQuasar();
const moneys = ref<MoneyDto[]>([]);
const infiniteScroll = ref<QInfiniteScroll | null>(null);
const moneyInEditStateCounter = useMoneyInEditStateStore();

function onLoad(index: number, done: () => void) {
  api
    .get('/money', {
      params: {
        sort: 'createdAt,DESC',
        size: 10,
        page: index - 1,
      },
    })
    .then((res: AxiosResponse<PageDto<MoneyDto>>) => {
      moneys.value.push(...res.data.content);
      done();
      if (res.data.last && infiniteScroll.value !== null) {
        // Stop infinite scroll
        infiniteScroll.value.stop();
      }
    })
    .catch((err: Error | AxiosError) => {
      if (isAxios401(err)) {
        refreshPage();
        return;
      }
      $q.notify({
        classes: 'q-px-lg',
        position: notifyPosition($q),
        progress: true,
        message: t('money.loading.error'),
        color: 'white',
        textColor: 'info',
        icon: 'img:icons/error.svg',
        iconColor: 'primary',
        iconSize: '20px',
      });
    });
}

function reloadMoney() {
  if (infiniteScroll.value === null) {
    return;
  }

  moneyInEditStateCounter.$reset();
  infiniteScroll.value.reset();
  moneys.value = [];
  infiniteScroll.value.resume();
  infiniteScroll.value.trigger();
}
</script>

<style>
.money-skeleton-text {
  min-height: 2.5rem;
}
</style>
