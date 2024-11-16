<template>
  <page-title
    :translatedTitle="$t('pages.budget.title')"
    :translatedSubtitle="$t('pages.budget.subtitle')"
  ></page-title>

  <q-infinite-scroll @load="onLoad" ref="infiniteScroll">
    <money-card
      v-for="money in moneys"
      :key="money.uuid"
      @reload-moneys="reloadMoneys"
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
</script>
