<template>
  <q-page class="column bg-1 text-1" padding>
    <q-input
      outlined
      autogrow
      autofocus
      clearable
      input-class="text-1"
      v-model="note"
      placeholder="Write something here, then save it"
      min-height="15rem"
      counter
      maxlength="255"
      :rules="[noteValidation]"
      ref="noteEditor"
    >
    </q-input>
    <q-btn
      unelevated
      padding="sm xl"
      aria-label="Save note"
      class="q-ml-auto q-mt-sm bg-2 text-2"
      :disable="noteSaveBtnDisabled"
      @click="saveNote"
      label="Save"
      ref="noteSaveBtn"
      :loading="noteSaveBtnLoading"
    ></q-btn>

    <q-infinite-scroll @load="onLoad" ref="infiniteScroll">
      <q-card
        v-for="note in notes"
        :key="note.uuid"
        class="q-mt-md bg-1 text-1"
        flat
        bordered
      >
        <q-card-section>
          {{ note.text }}
        </q-card-section>

        <q-separator />

        <q-card-actions>
          <q-btn flat disable label="Edit"></q-btn>
          <q-btn flat disable label="Delete"></q-btn>
        </q-card-actions>
      </q-card>

      <template v-slot:loading>
        <div class="column q-pa-lg justify-center items-center content-center">
          <q-spinner color="primary" size="3em" :thickness="2" />
        </div>
      </template>
    </q-infinite-scroll>
  </q-page>
</template>

<script setup lang="ts">
import { AxiosResponse } from 'axios';
import { QBtn, QInfiniteScroll, QInput, useQuasar } from 'quasar';
import { api } from 'src/boot/axios';
import { ref, watchEffect } from 'vue';

const note = ref('');
const noteEditor = ref<QInput | null>(null);
const noteSaveBtn = ref<QBtn | null>(null);
const noteSaveBtnLoading = ref(false);
const noteSaveBtnDisabled = ref(true);
const $q = useQuasar();
const notes = ref<NoteDtoWithUuid[]>([]);
const infiniteScroll = ref<QInfiniteScroll | null>(null);

interface NoteDto {
  uuid: string | null;
  text: string;
}

interface NoteDtoWithUuid {
  uuid: string;
  text: string;
}

interface PageDto<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  last: boolean;
  numberOfElements: number;
}

async function saveNote() {
  noteSaveBtnLoading.value = true;
  try {
    const noteDto: NoteDto = {
      uuid: null,
      text: note.value,
    };
    const res: AxiosResponse<NoteDto> = await api.post('/notes', noteDto);
    if (res.status === 201) {
      $q.notify({
        position: 'top-right',
        progress: true,
        message: 'Note correctly saved',
        color: 'white',
        textColor: 'info',
        icon: 'img:icons/success.svg',
        iconColor: 'primary',
        iconSize: '20px',
      });
      // Reload notes
      if (infiniteScroll.value !== null) {
        infiniteScroll.value.reset();
        notes.value = [];
        infiniteScroll.value.resume();
        infiniteScroll.value.trigger();
      }
      // Reset note text
      note.value = '';
    }
  } catch (err) {
    $q.notify({
      position: 'top-right',
      progress: true,
      message: 'Error saving note',
      color: 'white',
      textColor: 'info',
      icon: 'img:icons/error.svg',
      iconColor: 'primary',
      iconSize: '20px',
    });
  } finally {
    noteSaveBtnLoading.value = false;
  }
}

function onLoad(index: number, done: () => void) {
  api
    .get('/notes', {
      params: {
        sort: 'createdAt,DESC',
        size: 10,
        page: index - 1,
      },
    })
    .then((res: AxiosResponse<PageDto<NoteDtoWithUuid>>) => {
      notes.value.push(...res.data.content);
      done();
      if (res.data.last && infiniteScroll.value !== null) {
        infiniteScroll.value.stop();
        console.log('Stopping infinite scroll');
      }
    })
    .catch(() => {
      $q.notify({
        position: 'top-right',
        progress: true,
        message: 'Error loading notes',
        color: 'white',
        textColor: 'info',
        icon: 'img:icons/error.svg',
        iconColor: 'primary',
        iconSize: '20px',
      });
    });
}

const noteValidation = (note: string) =>
  !note || note === '' || note.replaceAll(' ', '').replaceAll('\n', '') === ''
    ? 'The note cannot be empty'
    : true;

watchEffect(() => {
  noteSaveBtnDisabled.value = noteValidation(note.value) !== true;
});
</script>
