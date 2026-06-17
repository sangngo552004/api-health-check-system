import React, { useEffect, useState } from "react";
import { useContactStore } from "../../store/useContactStore";
import {
  ContactGroupCreateCommand,
  ContactGroupDto,
  ContactGroupUpdateCommand,
} from "../../types/contact.types";
import { getErrorMessage } from "../../utils/error";
import { ContactForm } from "./ContactForm";
import { ContactsTable } from "./ContactsTable";
import { ContactsToolbar } from "./ContactsToolbar";

export const ContactsList: React.FC = () => {
  const {
    contactGroups,
    loading,
    fetchContactGroups,
    createContactGroup,
    updateContactGroup,
    deleteContactGroup,
  } = useContactStore();
  const [searchTerm, setSearchTerm] = useState("");
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingContact, setEditingContact] =
    useState<ContactGroupUpdateCommand | null>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    void fetchContactGroups(0, 100);
  }, [fetchContactGroups]);

  const filteredContacts = contactGroups.filter((contact) =>
    contact.name.toLowerCase().includes(searchTerm.toLowerCase()),
  );

  const handleAdd = () => {
    setEditingContact(null);
    setIsFormOpen(true);
  };

  const handleEdit = (contact: ContactGroupDto) => {
    setEditingContact({
      id: contact.id,
      name: contact.name,
      description: contact.description,
      isActive: contact.isActive,
      userIds: contact.userIds,
      emailAddresses: contact.emailAddresses,
      webhookUrls: contact.webhookUrls,
    });
    setIsFormOpen(true);
  };

  const handleDelete = async (contactId: number) => {
    if (!window.confirm("Ban co chac muon xoa Nhom lien he nay?")) {
      return;
    }

    await deleteContactGroup(contactId);
  };

  const handleFormSubmit = async (data: ContactGroupCreateCommand) => {
    setSubmitting(true);
    try {
      if (editingContact) {
        await updateContactGroup(editingContact.id, {
          ...data,
          id: editingContact.id,
        });
      } else {
        await createContactGroup(data);
      }
      setIsFormOpen(false);
    } catch (error) {
      alert(getErrorMessage(error));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div style={{ animation: "fadeIn 0.5s ease-out" }}>
      <ContactsToolbar
        searchTerm={searchTerm}
        onSearchTermChange={setSearchTerm}
        onCreate={handleAdd}
      />

      <ContactsTable
        loading={loading}
        contacts={filteredContacts}
        onEdit={handleEdit}
        onDelete={(contactId) => void handleDelete(contactId)}
      />

      {isFormOpen && (
        <ContactForm
          initialData={editingContact}
          loading={submitting}
          onSubmit={handleFormSubmit}
          onCancel={() => setIsFormOpen(false)}
        />
      )}
    </div>
  );
};
