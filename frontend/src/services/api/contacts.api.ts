import { api } from "../api";
import {
  ContactGroupDto,
  ContactGroupCreateCommand,
  ContactGroupUpdateCommand,
} from "../../types/contact.types";
import { PagedResponseDto } from "../../types/common.types";

export interface ContactGroupListParams {
  page?: number;
  size?: number;
  search?: string;
  isActive?: boolean;
  sortBy?: string;
  sortDir?: "asc" | "desc";
}

export const contactsApi = {
  getContactGroups: (params?: ContactGroupListParams) => {
    return api.get<PagedResponseDto<ContactGroupDto>>("/contact-groups", {
      params: params as Record<string, string | number | boolean | undefined>,
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
