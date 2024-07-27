export default {
  daily: {
    error: {
      default: 'Errore imprevisto',
      password: {
        change: {
          compromised:
            'La nuova password è compromessa e non dovrebbe essere utilizzata. Sceglierne una differente e riprovare',
          generic: 'Errore nel salvataggio della nuova password',
          mismatch: 'Le password inserite non corrispondono',
        },
      },
    },
  },
  language: {
    label: 'Lingua',
    english: 'Inglese',
    italian: 'Italiano',
  },
  dialog: {
    confirm: 'Conferma',
    cancel: 'Annulla',
    save: 'Salva',
    edit: 'Modifica',
    delete: 'Cancella',
  },
  pages: {
    notFound: {
      text: 'Ooops, qualcosa è andato storto',
      button: 'Home',
    },
    notes: { title: 'Note', subtitle: 'Annota ciò che desideri ricordare' },
    tags: {
      title: 'Tag',
      subtitle:
        'Etichette da associare per distinguere meglio le entità. Tocca un tag per modificarlo',
    },
    settings: {
      title: 'Impostazioni',
      subtitle: 'Le preferenze vengono salvate automaticamente',
    },
  },
  note: {
    placeholder: 'Scrivi qualcosa qui, poi salvalo',
    save: {
      button: 'Salva',
      error: 'Errore nel salvataggio della nota',
      ok: 'La nota è stata salvata correttamente',
      beingEdited:
        'Stai modificando note. Se continui, perderai le modifiche effettuate. Scegli <span class="text-1 text-weight-medium">OK</span> per annullare le modifiche e salvare la nota o <span class="text-1 text-weight-medium">ANNULLA</span> per continuare le modifiche e non salvare questa nota',
      validation: {
        empty: 'La nota non può essere vuota',
      },
    },
    delete: {
      confirm: 'Vuoi davvero cancellare la nota?',
      error: 'Errore nella cancellazione della nota',
      ok: 'La nota è stata cancellata correttamente',
    },
    loading: {
      error: 'Errore nel caricamento delle note',
    },
    created: 'Creata',
  },
  tag: {
    name: {
      placeholder: 'Scrivi qui il testo del tag, poi salvalo',
    },
    colorHex: {
      placeholder: 'Scegli il colore',
    },
    save: {
      button: 'Salva',
      error: 'Errore nel salvataggio del tag',
      ok: 'Il tag è stato salvato correttamente',
      validation: {
        name: {
          empty: 'Il nome del tag non può essere vuoto',
        },
        colorHex: {
          empty: 'Il colore del tag non può essere vuoto',
        },
      },
    },
    update: {
      error: 'Errore di aggiornamento del tag',
      ok: 'Il tag è stato aggiornato correttamente',
      dialog: {
        title: 'Aggiorna il tag',
        confirm: 'Conferma',
        cancel: 'Annulla',
      },
      confirm:
        'Se il tag è associato a una o più entità, la modifica verrà propagata. Sei sicuro di voler continuare?',
    },
    delete: {
      error: 'Errore nella cancellazione del tag',
      ok: 'Il tag è stato cancellato correttamente',
      confirm:
        'Se elimini il tag, verranno eliminate anche tutte le associazioni alle entità che hai fatto. Vuoi continuare?',
    },
    addToNote: {
      error: 'Errore di associazione del tag alla nota',
      ok: 'Tag correttamente associato alla nota',
      placeholder: 'Cerca tag da aggiungere',
      noResults: 'Nessun tag disponibile trovato',
    },
    removeFromNote: {
      error: 'Errore di rimozione del tag dalla nota',
      ok: 'Tag correttamente rimosso dalla nota',
    },
    loading: {
      error: 'Errore nel caricamento dei tag',
    },
  },
  settings: {
    dangerZone: {
      title: 'Danger zone',
      subtitle: 'Le seguenti operazioni non sono reversibili',
      passwordChange: {
        button: 'Cambia password',
      },
      deleteAppUser: { button: 'Elimina account' },
    },
  },
};
