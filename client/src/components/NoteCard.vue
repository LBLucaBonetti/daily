<template>
  <transition
    appear
    enter-active-class="animated fadeIn"
    leave-active-class="animated fadeOut"
  >
    <q-card class="q-mt-md bg-1 text-1" flat bordered>
      <q-card-section class="break-all">
        {{ note.text }}
      </q-card-section>

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
  </transition>

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
          :rules="[validateNote]"
          lazy-rules="ondemand"
          ref="noteUpdateInput"
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
          :loading="noteUpdateBtnLoading"
          label="Confirm"
          @click="confirmUpdateNote"
          aria-label="Confirm"
        />
      </q-card-actions> </q-card
  ></q-dialog>
</template>

<script setup lang="ts">
import { AxiosResponse } from 'axios';
import {
  QCard,
  QCardSection,
  QCardActions,
  QBtn,
  QInput,
  useQuasar,
} from 'quasar';
import { api } from 'src/boot/axios';
import NoteDto from 'src/interfaces/NoteDto';
import { PropType, ref } from 'vue';
import { validateNote } from 'src/validators/note-validator';
import { refreshPage } from 'src/utils/refresh-page';
import { isAxios401 } from 'src/utils/is-axios-401';

const $q = useQuasar();
const noteDeleteBtnLoading = ref(false);
const updateNoteDialog = ref(false);
const noteUpdateBtnLoading = ref(false);
const noteUpdateInput = ref<QInput | null>(null);
const noteTextUpdate = ref('');
const noteUuidUpdate = ref('');

defineProps({
  note: { type: Object as PropType<NoteDto>, required: true },
});

const emit = defineEmits(['reloadNotes']);

function updateNote(noteDto: NoteDto) {
  noteTextUpdate.value = noteDto.text;
  noteUuidUpdate.value = noteDto.uuid;
  updateNoteDialog.value = true;
}

async function deleteNote(noteDtoWithUuid: NoteDto) {
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
      emit('reloadNotes');
    }
  } catch (err) {
    if (isAxios401(err)) {
      refreshPage();
      return;
    }
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

async function confirmUpdateNote() {
  // Validate first
  if (!noteUpdateInput.value || noteUpdateInput.value.validate() !== true)
    return;
  // Set loading and do stuff
  noteUpdateBtnLoading.value = true;
  try {
    const updateNoteDto: NoteDto = {
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
      emit('reloadNotes');
      updateNoteDialog.value = false;
      noteTextUpdate.value = '';
      noteUuidUpdate.value = '';
    }
  } catch (err) {
    if (isAxios401(err)) {
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
    noteUpdateBtnLoading.value = false;
  }
}

function cancelUpdateNote() {
  updateNoteDialog.value = false;
  noteTextUpdate.value = '';
  noteUuidUpdate.value = '';
}
</script>
