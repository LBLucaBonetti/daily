<template>
  <q-btn
    color="red-8"
    unelevated
    :label="$t('settings.dangerZone.passwordChange.button')"
    aria-label="Change password"
    @click="passwordChangeDialog = true"
  ></q-btn>

  <!-- Password change dialog -->
  <q-dialog
    persistent
    v-model="passwordChangeDialog"
    noBackdropDismiss
    noEscDismiss
    @show="onShowPasswordChangeDialog"
    ><q-card class="bg-1 text-1"
      ><q-card-section
        ><div class="text-h6 text-1">
          {{ $t('settings.dangerZone.passwordChange.dialog.title') }}
        </div>
        <div class="text-subtitle-2 text-1">
          {{ $t('settings.dangerZone.passwordChange.dialog.subtitle') }}
        </div></q-card-section
      ><q-card-section class="q-pt-none"
        ><q-input
          outlined
          autofocus
          :type="passwordChangeCurrentPasswordType"
          input-class="text-1"
          v-model="passwordChangeCurrentPassword"
          :placeholder="
            $t(
              'settings.dangerZone.passwordChange.dialog.oldPassword.placeholder',
            )
          "
          :rules="[
            (val) =>
              validateOldPassword(
                val,
                $t(
                  'settings.dangerZone.passwordChange.dialog.oldPassword.empty',
                ),
              ),
          ]"
          lazy-rules="ondemand"
          class="password-change-input q-mb-md"
          ref="passwordChangeCurrentPasswordInput"
          ><template v-slot:append
            ><q-btn
              flat
              :icon="passwordChangeCurrentPasswordToggleVisibilityIcon"
              padding="none"
              dense
              ripple="false"
              round
              @click="
                togglePasswordChangeCurrentPasswordVisibility
              " /></template></q-input
        ><q-input
          outlined
          :type="passwordChangeNewPasswordType"
          input-class="text-1"
          v-model="passwordChangeNewPassword"
          :placeholder="
            $t(
              'settings.dangerZone.passwordChange.dialog.newPassword.placeholder',
            )
          "
          :rules="[
            (val) =>
              validateNewPassword(
                val,
                $t(
                  'settings.dangerZone.passwordChange.dialog.newPassword.empty',
                ),
              ),
          ]"
          lazy-rules="ondemand"
          class="password-change-input q-mb-md"
          ref="passwordChangeNewPasswordInput"
          ><template v-slot:append
            ><q-btn
              flat
              :icon="passwordChangeNewPasswordToggleVisibilityIcon"
              padding="none"
              dense
              ripple="false"
              round
              @click="
                togglePasswordChangeNewPasswordVisibility
              " /></template></q-input
        ><q-input
          outlined
          :type="passwordChangeNewPasswordConfirmationType"
          input-class="text-1"
          v-model="passwordChangeNewPasswordConfirmation"
          :placeholder="
            $t(
              'settings.dangerZone.passwordChange.dialog.newPasswordConfirmation.placeholder',
            )
          "
          :rules="[
            (val) =>
              validateNewPasswordConfirmation(
                val,
                $t(
                  'settings.dangerZone.passwordChange.dialog.newPasswordConfirmation.empty',
                ),
              ),
          ]"
          lazy-rules="ondemand"
          class="password-change-input"
          ref="passwordChangeNewPasswordConfirmationInput"
          ><template v-slot:append
            ><q-btn
              flat
              :icon="passwordChangeNewPasswordConfirmationToggleVisibilityIcon"
              padding="none"
              dense
              ripple="false"
              round
              @click="
                togglePasswordChangeNewPasswordConfirmationVisibility
              " /></template></q-input></q-card-section
      ><q-card-actions align="right">
        <q-btn
          class="bg-1 text-1"
          flat
          :label="$t('dialog.cancel')"
          @click="cancelPasswordChange"
          ref="passwordChangeCancelBtn"
        />
        <q-btn
          class="bg-1 text-1"
          flat
          :label="$t('dialog.confirm')"
          @click="askConfirmationToChangePassword"
        /> </q-card-actions></q-card
  ></q-dialog>
</template>
<script setup lang="ts">
import {
  useQuasar,
  QBtn,
  QDialog,
  QCard,
  QCardSection,
  QCardActions,
  QInput,
  QInputType,
} from 'quasar';
import { Ref, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import PasswordChangeDto from 'src/interfaces/PasswordChangeDto';
import ErrorDto from 'src/interfaces/ErrorDto';
import { AxiosError, AxiosResponse } from 'axios';
import { isAxios401 } from 'src/utils/is-axios-401';
import { notifyPosition } from 'src/utils/notify-position';
import { refreshPage } from 'src/utils/refresh-page';
import { api } from 'src/boot/axios';
import {
  validateOldPassword,
  validateNewPassword,
  validateNewPasswordConfirmation,
} from 'src/validators/password-change-validator';

const $q = useQuasar();
const { t } = useI18n();

const passwordChangeDialog = ref(false);
const passwordChangeCurrentPassword = ref('');
const passwordChangeCurrentPasswordInput = ref<QInput | null>(null);
const passwordChangeCurrentPasswordType = ref<QInputType>('password');
const passwordChangeCurrentPasswordToggleVisibilityIcon = ref(
  'svguse:icons/svg-defs.svg#eye-open|0 0 256 256',
);
const passwordChangeNewPassword = ref('');
const passwordChangeNewPasswordInput = ref<QInput | null>(null);
const passwordChangeNewPasswordType = ref<QInputType>('password');
const passwordChangeNewPasswordToggleVisibilityIcon = ref(
  'svguse:icons/svg-defs.svg#eye-open|0 0 256 256',
);
const passwordChangeNewPasswordConfirmation = ref('');
const passwordChangeNewPasswordConfirmationInput = ref<QInput | null>(null);
const passwordChangeNewPasswordConfirmationType = ref<QInputType>('password');
const passwordChangeNewPasswordConfirmationToggleVisibilityIcon = ref(
  'svguse:icons/svg-defs.svg#eye-open|0 0 256 256',
);
const passwordChangeCancelBtn = ref<QBtn | null>(null);

const onShowPasswordChangeDialog = () => {
  // Focus on cancel
  passwordChangeCancelBtn.value?.$el.focus();
};
const cancelPasswordChange = () => {
  resetPasswordChangeFields();
  passwordChangeDialog.value = false;
};
const resetPasswordChangeFields = () => {
  // Reset password change dialog fields
  passwordChangeCurrentPassword.value = '';
  passwordChangeNewPassword.value = '';
  passwordChangeNewPasswordConfirmation.value = '';
};
const askConfirmationToChangePassword = () => {
  // Validate first
  if (
    !passwordChangeCurrentPasswordInput.value ||
    passwordChangeCurrentPasswordInput.value.validate() !== true
  )
    return;
  if (
    !passwordChangeNewPasswordInput.value ||
    passwordChangeNewPasswordInput.value.validate() !== true
  )
    return;
  if (
    !passwordChangeNewPasswordConfirmationInput.value ||
    passwordChangeNewPasswordConfirmationInput.value.validate() !== true
  )
    return;
  $q.dialog({
    title: t('dialog.confirm'),
    message: t('settings.dangerZone.passwordChange.dialog.confirm'),
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
    changePassword();
  });
};
const changePassword = async () => {
  try {
    const passwordChangeDto: PasswordChangeDto = {
      oldPassword: passwordChangeCurrentPassword.value,
      newPassword: passwordChangeNewPassword.value,
      newPasswordConfirmation: passwordChangeNewPasswordConfirmation.value,
    };
    const res: AxiosResponse<ErrorDto> = await api.put(
      '/appusers/passwords',
      passwordChangeDto,
    );
    if (res.status === 204) {
      // Show ok message
      $q.notify({
        classes: 'q-px-lg',
        position: notifyPosition($q),
        progress: true,
        message: t('settings.dangerZone.passwordChange.ok'),
        color: 'white',
        textColor: 'info',
        icon: 'img:icons/success.svg',
        iconColor: 'primary',
        iconSize: '20px',
      });
    } else {
      // Show error message
      $q.notify({
        classes: 'q-px-lg',
        position: notifyPosition($q),
        progress: true,
        message: t(res.data.error),
        color: 'white',
        textColor: 'info',
        icon: 'img:icons/error.svg',
        iconColor: 'primary',
        iconSize: '20px',
      });
    }
  } catch (err) {
    if (isAxios401(err)) {
      refreshPage();
      return;
    }
    const parsedError = err as AxiosError<ErrorDto>;
    // Show error message
    $q.notify({
      classes: 'q-px-lg',
      position: notifyPosition($q),
      progress: true,
      message: t(
        parsedError.response?.data.error
          ? parsedError.response.data.error
          : 'daily.error.password.change.generic',
      ),
      color: 'white',
      textColor: 'info',
      icon: 'img:icons/error.svg',
      iconColor: 'primary',
      iconSize: '20px',
    });
  } finally {
    // Close dialog
    passwordChangeDialog.value = false;
    resetPasswordChangeFields();
  }
};

const togglePasswordChangeCurrentPasswordVisibility = () => {
  togglePasswordInputVisibility(
    passwordChangeCurrentPasswordType,
    passwordChangeCurrentPasswordToggleVisibilityIcon,
  );
};

const togglePasswordChangeNewPasswordVisibility = () => {
  togglePasswordInputVisibility(
    passwordChangeNewPasswordType,
    passwordChangeNewPasswordToggleVisibilityIcon,
  );
};

const togglePasswordChangeNewPasswordConfirmationVisibility = () => {
  togglePasswordInputVisibility(
    passwordChangeNewPasswordConfirmationType,
    passwordChangeNewPasswordConfirmationToggleVisibilityIcon,
  );
};

const togglePasswordInputVisibility = (
  inputTypeRef: Ref<QInputType>,
  toggleVisibilityIconRef: Ref<string>,
) => {
  if (!inputTypeRef.value) {
    return;
  }
  if (inputTypeRef.value === 'password') {
    inputTypeRef.value = 'text';
    toggleVisibilityIconRef.value =
      'svguse:icons/svg-defs.svg#eye-closed|0 0 256 256';
    return;
  }
  inputTypeRef.value = 'password';
  toggleVisibilityIconRef.value =
    'svguse:icons/svg-defs.svg#eye-open|0 0 256 256';
};
</script>
<style>
.password-change-input {
  min-width: 280px;
}
</style>
