<template>
  <q-page class="row content-start bg-1 text-1 q-pa-md">
    <div class="row q-mx-auto col-xs-12 col-sm-10 col-md-8">
      <q-input
        outlined
        autogrow
        autofocus
        clearable
        class="col-12"
        input-class="text-1"
        v-model="note"
        placeholder="Write something here, then save it"
        min-height="15rem"
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
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { AxiosResponse } from 'axios';
import { QBtn, QInput } from 'quasar';
import { api } from 'src/boot/axios';
import { ref, watchEffect } from 'vue';

const note = ref('');
const noteEditor = ref<QInput | null>(null);
const noteSaveBtn = ref<QBtn | null>(null);
const noteSaveBtnLoading = ref(false);
const noteSaveBtnDisabled = ref(true);

interface NoteDto {
  uuid: string | null;
  text: string;
}

async function saveNote() {
  noteSaveBtnLoading.value = true;
  try {
    const noteDto: NoteDto = {
      uuid: null,
      text: note.value,
    };
    const res: AxiosResponse<NoteDto> = await api.post('/notes', noteDto);
    console.log(res.data);
  } catch (err) {
    console.error('Uncaught error');
  } finally {
    noteSaveBtnLoading.value = false;
  }
}

const noteValidation = (note: string) =>
  !note || note === '' || note.replaceAll(' ', '').replaceAll('\n', '') === ''
    ? 'The note cannot be empty'
    : true;

watchEffect(() => {
  noteSaveBtnDisabled.value = noteValidation(note.value) !== true;
});
</script>
