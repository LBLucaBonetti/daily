<template>
  <transition
    appear
    enter-active-class="animated fadeIn"
    leave-active-class="animated fadeOut"
  >
    <q-card
      :class="
        moneyEditable
          ? 'q-mt-md bg-1 text-1 border-primary'
          : 'q-mt-md bg-1 text-1'
      "
      flat
      bordered
    >
      <q-card-section class="money-text">
        <q-input
          autogrow
          autofocus
          clearable
          input-class="text-1"
          v-model="moneyUpdateText"
          min-height="15rem"
          counter
          maxlength="255"
          borderless
          :rules="[
            (val) => validateMoney(val, $t('money.save.validation.empty')),
          ]"
          lazy-rules="ondemand"
          ref="moneyUpdateInput"
          v-if="moneyEditable"
        >
        </q-input>
        <!-- TODO https://www.npmjs.com/package/v-money3 & https://github.com/jonathanpmartins/v-money3 -->
        <q-input
          autogrow
          input-class="text-1"
          v-model="moneyText"
          min-height="15rem"
          counter
          maxlength="255"
          borderless
          readonly
          v-else
        ></q-input>
      </q-card-section>

      <q-card-section>
        <q-select
          borderless
          dense
          v-model="addTagToMoneySelectModel"
          use-input
          :multiple="false"
          hide-dropdown-icon
          hide-selected
          input-debounce="500"
          input-class="text-1"
          :options="addTagToMoneySelectOptions"
          option-label="name"
          @filter="addTagToMoneySelectFilter"
          @update:model-value="addTagToMoney"
          :label="$t('tag.addToMoney.placeholder')"
        >
          <template v-slot:no-option>
            <q-item>
              <q-item-section class="text-1">{{
                $t('tag.addToMoney.noResults')
              }}</q-item-section>
            </q-item>
          </template>
        </q-select>
        <div class="row q-mt-md" style="gap: 24px">
          <tag-chip
            v-for="tag in tags"
            :key="tag.uuid"
            :tag="tag"
            @remove="removeTagFromMoney(tag)"
          ></tag-chip>
        </div>
      </q-card-section>

      <q-card-section>
        <q-card-actions class="no-padding" :vertical="$q.screen.lt.sm">
          <div :class="$q.screen.lt.sm ? 'text-3 text-center' : 'text-3'">
            {{
              $t('money.created') +
              ' ' +
              new Date(money.createdAt?.toString() + 'Z')?.toLocaleString(
                languageStore.language,
              )
            }}
          </div>
          <q-space></q-space>
          <template v-if="moneyEditable">
            <q-btn
              flat
              :label="$t('dialog.cancel')"
              aria-label="Cancel"
              @click="cancelEditMoney"
            ></q-btn>
            <q-btn
              unelevated
              :loading="moneyUpdateBtnLoading"
              :disable="moneyText === moneyUpdateText"
              :label="$t('dialog.save')"
              aria-label="Save"
              @click="updateMoney"
              color="primary"
            />
          </template>
          <template v-else>
            <q-btn
              flat
              :label="$t('dialog.edit')"
              aria-label="Edit"
              @click="editMoney"
            ></q-btn>
            <q-btn
              unelevated
              :loading="moneyDeleteBtnLoading"
              :label="$t('dialog.delete')"
              @click="askConfirmationToDeleteMoney"
              aria-label="Delete"
              color="negative"
            ></q-btn>
          </template>
        </q-card-actions>
      </q-card-section>
    </q-card>
  </transition>
</template>

<script setup lang="ts">
import { validateMoney } from 'src/validators/money-validator';
import type { AxiosError, AxiosResponse } from 'axios';
import {
  QCard,
  QCardSection,
  QCardActions,
  QBtn,
  useQuasar,
  QInput,
} from 'quasar';
import { api } from 'src/boot/axios';
import type MoneyDto from 'src/interfaces/MoneyDto';
import { onMounted, type PropType, ref } from 'vue';
import { refreshPage } from 'src/utils/refresh-page';
import { isAxios401 } from 'src/utils/is-axios-401';
import { notifyPosition } from 'src/utils/notify-position';
import { useMoneyInEditStateStore } from 'src/stores/moneyEditingStore';
import { useI18n } from 'vue-i18n';
import { useLanguageStore } from 'src/stores/languageStore';
import type TagDto from 'src/interfaces/TagDto';
import TagChip from '../components/TagChip.vue';
import type PageDto from 'src/interfaces/PageDto';

const { t } = useI18n();
const $q = useQuasar();
const moneyText = ref('');
const moneyDeleteBtnLoading = ref(false);
const moneyEditable = ref(false);
const moneyUpdateText = ref('');
const moneyUpdateBtnLoading = ref(false);
const moneyUpdateInput = ref<QInput | null>(null);
const moneyInEditStateCounter = useMoneyInEditStateStore();
const languageStore = useLanguageStore();
const tags = ref<TagDto[]>([]);
const addTagToMoneySelectModel = ref<TagDto | null>(null);
const addTagToMoneySelectOptions = ref<TagDto[]>([]);

const props = defineProps({
  money: { type: Object as PropType<MoneyDto>, required: true },
});

const emit = defineEmits(['reloadMoney']);

onMounted(() => {
  moneyText.value = props.money.text;
  loadTags();
});

function reloadTags() {
  loadTags();
}

function loadTags() {
  api
    .get('/money/' + props.money.uuid + '/tags')
    .then((res: AxiosResponse<TagDto[]>) => {
      tags.value = [...res.data];
    })
    .catch((err: Error | AxiosError) => {
      if (isAxios401(err)) {
        refreshPage();
        return;
      }
      console.error(
        'Could not retrieve tags for money with id ' + props.money.uuid,
      );
    });
}

async function updateMoney() {
  // Validate first
  if (!moneyUpdateInput.value || moneyUpdateInput.value.validate() !== true)
    return;
  // Set loading and do stuff
  moneyUpdateBtnLoading.value = true;
  try {
    const updateMoneyDto: MoneyDto = {
      text: moneyUpdateText.value,
      uuid: props.money.uuid,
    };
    const res: AxiosResponse<MoneyDto> = await api.put(
      '/money/' + updateMoneyDto.uuid,
      updateMoneyDto,
    );
    if (res.status === 200) {
      $q.notify({
        classes: 'q-px-lg',
        position: notifyPosition($q),
        progress: true,
        message: t('money.save.ok'),
        color: 'white',
        textColor: 'info',
        icon: 'img:icons/success.svg',
        iconColor: 'primary',
        iconSize: '20px',
      });
      // Restore non-editable state
      moneyText.value = moneyUpdateText.value;
      moneyInEditStateCounter.decrementMoneyInEditState();
      moneyEditable.value = false;
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
      message: t('money.save.error'),
      color: 'white',
      textColor: 'info',
      icon: 'img:icons/error.svg',
      iconColor: 'primary',
      iconSize: '20px',
    });
  } finally {
    moneyUpdateBtnLoading.value = false;
  }
}

function editMoney() {
  moneyUpdateText.value = moneyText.value;
  moneyInEditStateCounter.incrementMoneyInEditState();
  moneyEditable.value = true;
}

function cancelEditMoney() {
  moneyInEditStateCounter.decrementMoneyInEditState();
  moneyEditable.value = false;
}

function askConfirmationToDeleteMoney() {
  $q.dialog({
    title: t('dialog.confirm'),
    message: t('money.delete.confirm'),
    persistent: true,
    class: 'bg-1 text-1',
    ok: {
      flat: true,
      color: 'negative',
    },
    cancel: {
      flat: true,
      class: 'bg-1 text-1',
    },
    focus: 'cancel',
  }).onOk(() => {
    deleteMoney();
  });
}

async function deleteMoney() {
  moneyDeleteBtnLoading.value = true;
  try {
    const res: AxiosResponse<MoneyDto> = await api.delete(
      '/money/' + props.money.uuid,
    );
    if (res.status === 204) {
      $q.notify({
        classes: 'q-px-lg',
        position: notifyPosition($q),
        progress: true,
        message: t('money.delete.ok'),
        color: 'white',
        textColor: 'info',
        icon: 'img:icons/success.svg',
        iconColor: 'primary',
        iconSize: '20px',
      });
      // Reload moneys
      emit('reloadMoney');
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
      message: t('money.delete.error'),
      color: 'white',
      textColor: 'info',
      icon: 'img:icons/error.svg',
      iconColor: 'primary',
      iconSize: '20px',
    });
  } finally {
    moneyDeleteBtnLoading.value = false;
  }
}

function addTagToMoneySelectFilter(
  userProvidedName: string,
  update: (doneFn: () => void) => void,
) {
  update(() => {
    if (userProvidedName === '') return;
    // Search tags
    api
      .get('/tags', { params: { name: userProvidedName } })
      .then((res: AxiosResponse<PageDto<TagDto>>) => {
        // Replace the content of the options each time
        addTagToMoneySelectOptions.value = [...res.data.content];
      })
      .catch((err: Error | AxiosError) => {
        if (isAxios401(err)) {
          refreshPage();
          return;
        }
        console.error('Could not search tags');
      });
  });
}

async function addTagToMoney(value: TagDto) {
  try {
    if (!value) {
      return;
    }

    // Add tag to money, if it's already present for the current money, it should not be a problem
    const res: AxiosResponse<TagDto> = await api.put(
      '/money/' + props.money.uuid + '/tags/' + value.uuid,
    );
    if (res.status === 204) {
      $q.notify({
        classes: 'q-px-lg',
        position: notifyPosition($q),
        progress: true,
        message: t('tag.addToMoney.ok'),
        color: 'white',
        textColor: 'info',
        icon: 'img:icons/success.svg',
        iconColor: 'primary',
        iconSize: '20px',
      });
      reloadTags();
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
      message: t('tag.addToMoney.error'),
      color: 'white',
      textColor: 'info',
      icon: 'img:icons/error.svg',
      iconColor: 'primary',
      iconSize: '20px',
    });
  } finally {
    // Reset select options anyway
    addTagToMoneySelectModel.value = null;
    addTagToMoneySelectOptions.value = [];
  }
}

async function removeTagFromMoney(tag: TagDto) {
  try {
    // Remove tag from money
    const res: AxiosResponse<TagDto> = await api.delete(
      '/money/' + props.money.uuid + '/tags/' + tag.uuid,
    );
    if (res.status === 204) {
      $q.notify({
        classes: 'q-px-lg',
        position: notifyPosition($q),
        progress: true,
        message: t('tag.removeFromMoney.ok'),
        color: 'white',
        textColor: 'info',
        icon: 'img:icons/success.svg',
        iconColor: 'primary',
        iconSize: '20px',
      });
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
      message: t('tag.removeFromMoney.error'),
      color: 'white',
      textColor: 'info',
      icon: 'img:icons/error.svg',
      iconColor: 'primary',
      iconSize: '20px',
    });
  } finally {
    // Reload tags anyway
    reloadTags();
  }
}
</script>
