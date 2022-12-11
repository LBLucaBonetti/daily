<template>
  <q-page class="text-1 w-sm-50" padding>
    <q-input
      outlined
      autogrow
      autofocus
      clearable
      input-class="text-1"
      v-model="note"
      placeholder="Write something here, then save it"
      counter
      maxlength="255"
      :rules="[validateNote]"
      lazy-rules="ondemand"
      ref="noteInput"
    >
    </q-input>
    <div class="row justify-end">
      <q-btn
        unelevated
        padding="sm xl"
        aria-label="Save note"
        class="q-mt-sm bg-2 text-2"
        @click="saveNote"
        label="Save"
        ref="noteSaveBtn"
        :loading="noteSaveBtnLoading"
      ></q-btn>
    </div>

    <q-infinite-scroll @load="onLoad" ref="infiniteScroll">
      <note-card
        v-for="note in notes"
        :key="note.uuid"
        @reload-notes="reloadNotes"
        :note="note"
      />

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
import { ref } from 'vue';
import NoteCard from '../components/NoteCard.vue';
import { NoteDto } from 'src/interfaces/NoteDto';
import { validateNote } from 'src/validators/note-validator';
import { PageDto } from 'src/interfaces/PageDto';
import { refreshPage } from 'src/utils/refresh-page';

const note = ref('');
const noteInput = ref<QInput | null>(null);
const noteSaveBtn = ref<QBtn | null>(null);
const noteSaveBtnLoading = ref(false);
const $q = useQuasar();
const notes = ref<NoteDto[]>([]);
const infiniteScroll = ref<QInfiniteScroll | null>(null);

async function saveNote() {
  // Validate first
  if (!noteInput.value || noteInput.value.validate() !== true) return;
  // Set loading and do stuff
  noteSaveBtnLoading.value = true;
  try {
    const noteDto: NoteDto = {
      uuid: '',
      text: note.value,
    };
    const res: AxiosResponse<NoteDto> = await api.post('/notes', noteDto);
    if (res.status === 201) {
      $q.notify({
        classes: 'q-px-lg',
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
      reloadNotes();
      // Reset note text
      note.value = '';
      // Set focus back to input
      noteInput.value.focus();
    }
  } catch (err: any) {
    if (err.response?.status === 401) {
      refreshPage();
      return;
    }
    $q.notify({
      classes: 'q-px-lg',
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
    .then((res: AxiosResponse<PageDto<NoteDto>>) => {
      notes.value.push(...res.data.content);
      done();
      if (res.data.last && infiniteScroll.value !== null) {
        // Stop infinite scroll
        infiniteScroll.value.stop();
      }
    })
    .catch((err) => {
      if (err.response?.status === 401) {
        refreshPage();
        return;
      }
      $q.notify({
        classes: 'q-px-lg',
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

function reloadNotes() {
  if (infiniteScroll.value === null) {
    return;
  }

  infiniteScroll.value.reset();
  notes.value = [];
  infiniteScroll.value.resume();
  infiniteScroll.value.trigger();
}
</script>
