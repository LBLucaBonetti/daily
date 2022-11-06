<template>
  <q-page class="bg-1 text-1" padding>
    <q-dialog v-model="updateNoteDialog"
      ><q-card>
        <q-card-section>
          <div class="text-h6">Update note</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-input
            outlined
            autogrow
            autofocus
            clearable
            input-class="text-1"
            v-model="noteTextUpdate"
            min-height="15rem"
            counter
            maxlength="255"
            :rules="[noteValidation]"
          >
          </q-input>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn
            class="bg-1 text-1"
            flat
            label="Cancel"
            @click="cancelUpdateNote"
            aria-label="Cancel"
          />
          <q-btn
            class="bg-1 text-1"
            flat
            :disable="noteUpdateBtnDisabled"
            :loading="noteUpdateBtnLoading"
            label="Confirm"
            @click="confirmUpdateNote"
            aria-label="Confirm"
          />
        </q-card-actions> </q-card
    ></q-dialog>

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
    >
    </q-input>
    <div class="row justify-end">
      <q-btn
        unelevated
        padding="sm xl"
        aria-label="Save note"
        class="q-mt-sm bg-2 text-2"
        :disable="noteSaveBtnDisabled"
        @click="saveNote"
        label="Save"
        ref="noteSaveBtn"
        :loading="noteSaveBtnLoading"
      ></q-btn>
    </div>

    <q-infinite-scroll @load="onLoad" ref="infiniteScroll">
      <q-card
        v-for="note in notes"
        :key="note.uuid"
        class="q-mt-md bg-1 text-1"
        flat
        bordered
      >
        <q-card-section class="break-all">
          {{ note.text }}
        </q-card-section>

        <q-separator />

        <q-card-actions align="right">
          <q-btn flat label="Edit" @click="updateNote(note)"></q-btn>
          <q-btn
            flat
            :loading="noteDeleteBtnLoading"
            label="Delete"
            @click="deleteNote(note)"
            aria-label="Delete"
          ></q-btn>
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
const noteTextUpdate = ref('');
const noteUuidUpdate = ref('');
const noteSaveBtn = ref<QBtn | null>(null);
const noteSaveBtnLoading = ref(false);
const noteSaveBtnDisabled = ref(true);
const noteUpdateBtnLoading = ref(false);
const noteUpdateBtnDisabled = ref(true);
const noteDeleteBtnLoading = ref(false);
const updateNoteDialog = ref(false);
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

function updateNote(noteDtoWithUuid: NoteDtoWithUuid) {
  const noteToUpdate = notes.value.find(
    (note) => note.uuid === noteDtoWithUuid.uuid
  );
  if (!noteToUpdate) return;
  noteTextUpdate.value = noteToUpdate.text;
  noteUuidUpdate.value = noteToUpdate.uuid;
  updateNoteDialog.value = true;
}

function cancelUpdateNote() {
  updateNoteDialog.value = false;
  noteTextUpdate.value = '';
  noteUuidUpdate.value = '';
}

async function confirmUpdateNote() {
  noteUpdateBtnLoading.value = true;
  try {
    const updateNoteDto: NoteDtoWithUuid = {
      text: noteTextUpdate.value,
      uuid: noteUuidUpdate.value,
    };
    const res: AxiosResponse<NoteDto> = await api.put(
      '/notes/' + updateNoteDto.uuid,
      updateNoteDto
    );
    if (res.status === 204) {
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
      if (infiniteScroll.value !== null) {
        infiniteScroll.value.reset();
        notes.value = [];
        infiniteScroll.value.resume();
        infiniteScroll.value.trigger();
      }
      updateNoteDialog.value = false;
      noteTextUpdate.value = '';
      noteUuidUpdate.value = '';
    }
  } catch (err) {
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
    noteUpdateBtnLoading.value = false;
  }
}

async function deleteNote(noteDtoWithUuid: NoteDtoWithUuid) {
  noteDeleteBtnLoading.value = true;
  try {
    const res: AxiosResponse<NoteDto> = await api.delete(
      '/notes/' + noteDtoWithUuid.uuid
    );
    if (res.status === 204) {
      $q.notify({
        classes: 'q-px-lg',
        position: 'top-right',
        progress: true,
        message: 'Note correctly deleted',
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
    }
  } catch (err) {
    $q.notify({
      classes: 'q-px-lg',
      position: 'top-right',
      progress: true,
      message: 'Error deleting note',
      color: 'white',
      textColor: 'info',
      icon: 'img:icons/error.svg',
      iconColor: 'primary',
      iconSize: '20px',
    });
  } finally {
    noteDeleteBtnLoading.value = false;
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

const noteValidation = (note: string) =>
  !note || note === '' || note.replaceAll(' ', '').replaceAll('\n', '') === ''
    ? 'The note cannot be empty'
    : true;

watchEffect(() => {
  noteSaveBtnDisabled.value = noteValidation(note.value) !== true;
  noteUpdateBtnDisabled.value = noteValidation(noteTextUpdate.value) !== true;
});
</script>
