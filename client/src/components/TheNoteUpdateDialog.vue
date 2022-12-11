<template>
  <q-dialog ref="dialogRef" @hide="onDialogHide"
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
          @click="onDialogCancel"
          aria-label="Cancel"
        />
        <q-btn
          class="bg-1 text-1"
          flat
          :loading="noteUpdateBtnLoading"
          label="Confirm"
          @click="onConfirmClick"
          aria-label="Confirm"
        />
      </q-card-actions> </q-card
  ></q-dialog>
</template>

<script setup lang="ts">
import { validateNote } from 'src/validators/note-validator';
import { onMounted, PropType, ref } from 'vue';
import {
  QBtn,
  QCard,
  QCardActions,
  QCardSection,
  QDialog,
  QInput,
  useDialogPluginComponent,
  useQuasar,
} from 'quasar';
import { AxiosResponse } from 'axios';
import { api } from 'src/boot/axios';
import NoteDto from 'src/interfaces/NoteDto';
import { isAxios401 } from 'src/utils/is-axios-401';
import { refreshPage } from 'src/utils/refresh-page';

const props = defineProps({
  note: { type: Object as PropType<NoteDto>, required: true },
});

defineEmits([...useDialogPluginComponent.emits]);

const { dialogRef, onDialogHide, onDialogOK, onDialogCancel } =
  useDialogPluginComponent();
const $q = useQuasar();

const noteUpdateBtnLoading = ref(false);
const noteUpdateInput = ref<QInput | null>(null);
const noteTextUpdate = ref('');

onMounted(() => {
  noteTextUpdate.value = props.note.text;
});

async function onConfirmClick() {
  // Validate first
  if (!noteUpdateInput.value || noteUpdateInput.value.validate() !== true)
    return;
  // Set loading and do stuff
  noteUpdateBtnLoading.value = true;
  try {
    const updateNoteDto: NoteDto = {
      text: noteTextUpdate.value,
      uuid: props.note.uuid,
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
      // Settle the dialog
      onDialogOK();
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
</script>
