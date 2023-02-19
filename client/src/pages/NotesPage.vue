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
        @click="askConfirmationIfThereAreNotesInEditState"
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
        <q-card class="q-mt-md bg-1 text-1" flat bordered>
          <q-card-section class="q-pa-sm">
            <q-skeleton type="text" class="note-skeleton-text"></q-skeleton>
          </q-card-section>

          <q-card-actions class="q-pa-sm" align="right">
            <q-skeleton type="QBtn"></q-skeleton>
            <q-skeleton type="QBtn" class="q-ml-sm"></q-skeleton>
          </q-card-actions>
        </q-card>
      </template>
    </q-infinite-scroll>
  </q-page>
</template>

<script setup lang="ts">
import { AxiosError, AxiosResponse } from 'axios';
import {
  QBtn,
  QInfiniteScroll,
  QInput,
  QPage,
  QSkeleton,
  useQuasar,
} from 'quasar';
import { api } from 'src/boot/axios';
import { ref } from 'vue';
import NoteCard from '../components/NoteCard.vue';
import NoteDto from 'src/interfaces/NoteDto';
import { validateNote } from 'src/validators/note-validator';
import PageDto from 'src/interfaces/PageDto';
import { refreshPage } from 'src/utils/refresh-page';
import { isAxios401 } from 'src/utils/is-axios-401';
import { notifyPosition } from 'src/utils/notify-position';
import { useNotesInEditStateStore } from 'src/stores/noteEditingStore';

const note = ref('');
const noteInput = ref<QInput | null>(null);
const noteSaveBtn = ref<QBtn | null>(null);
const noteSaveBtnLoading = ref(false);
const $q = useQuasar();
const notes = ref<NoteDto[]>([]);
const infiniteScroll = ref<QInfiniteScroll | null>(null);
const notesInEditStateCounter = useNotesInEditStateStore();

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
        position: notifyPosition($q),
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
  } catch (err) {
    if (isAxios401(err)) {
      refreshPage();
      return;
    }
    $q.notify({
      classes: 'q-px-lg',
      position: notifyPosition($q),
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

function askConfirmationIfThereAreNotesInEditState() {
  if (notesInEditStateCounter.counter <= 0) {
    saveNote();
    return;
  }
  $q.dialog({
    title: 'Confirm',
    message:
      'You currently have ' +
      notesInEditStateCounter.counter +
      (notesInEditStateCounter.counter == 1 ? ' note' : ' notes') +
      ' being edited. If you proceed, you will lose your changes to them. Choose <span class="text-1 text-weight-medium">OK</span> to discard changes and save the note or <span class="text-1 text-weight-medium">CANCEL</span> to keep editing them and avoid saving this note',
    html: true,
    persistent: true,
    class: 'bg-1 text-1',
    ok: {
      flat: true,
      class: 'bg-1 text-1',
    },
    cancel: {
      flat: true,
      class: 'bg-1 text-1',
    },
    focus: 'cancel',
  }).onOk(() => {
    saveNote();
  });
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
    .catch((err: Error | AxiosError) => {
      if (isAxios401(err)) {
        refreshPage();
        return;
      }
      $q.notify({
        classes: 'q-px-lg',
        position: notifyPosition($q),
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

  notesInEditStateCounter.$reset();
  infiniteScroll.value.reset();
  notes.value = [];
  infiniteScroll.value.resume();
  infiniteScroll.value.trigger();
}
</script>

<style>
.note-skeleton-text {
  min-height: 2.5rem;
}
</style>
