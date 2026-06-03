import { api } from "../api";
import {
  ContactGroupDto,
  ContactGroupCreateCommand,
  ContactGroupUpdateCommand,
} from "../../types/contact.types";
import { PagedResponseDto } from "../../types/common.types";

export const contactsApi = {
  getContactGroups: (page = 0, size = 10) => {
    return api.get<PagedResponseDto<ContactGroupDto>>("/contact-groups", {
      params: { page, size },
    });
  },

  getContactGroupById: (id: number) => {
    return api.get<ContactGroupDto>(`/contact-groups/${id}`);
  },

  createContactGroup: (data: ContactGroupCreateCommand) => {
    return api.post<ContactGroupDto>("/contact-groups", data);
  },

  updateContactGroup: (id: number, data: ContactGroupUpdateCommand) => {
    return api.put<ContactGroupDto>(`/contact-groups/${id}`, data);
  },

  deleteContactGroup: (id: number) => {
    return api.delete<void>(`/contact-groups/${id}`);
  },
};
