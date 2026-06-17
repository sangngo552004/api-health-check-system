import { create } from "zustand";
import { contactsApi } from "../services/api/contacts.api";
import {
  ContactGroupDto,
  ContactGroupCreateCommand,
  ContactGroupUpdateCommand,
} from "../types/contact.types";
import { getErrorMessage } from "../utils/error";

interface ContactState {
  contactGroups: ContactGroupDto[];
  loading: boolean;
  error: string | null;
  totalItems: number;
  totalPages: number;
  currentPage: number;

  fetchContactGroups: (page?: number, size?: number) => Promise<void>;
  createContactGroup: (data: ContactGroupCreateCommand) => Promise<void>;
  updateContactGroup: (
    id: number,
    data: ContactGroupUpdateCommand,
  ) => Promise<void>;
  deleteContactGroup: (id: number) => Promise<void>;
}

export const useContactStore = create<ContactState>((set, get) => ({
  contactGroups: [],
  loading: false,
  error: null,
  totalItems: 0,
  totalPages: 0,
  currentPage: 0,

  fetchContactGroups: async (page = 0, size = 10) => {
    set({ loading: true, error: null });
    try {
      const res = await contactsApi.getContactGroups(page, size);
      set({
        contactGroups: res.items,
        totalItems: res.totalItems,
        totalPages: res.totalPages,
        currentPage: page,
        loading: false,
      });
    } catch (error) {
      set({
        error: getErrorMessage(error, "Lỗi khi tải danh sách Contact Groups"),
        loading: false,
      });
    }
  },

  createContactGroup: async (data: ContactGroupCreateCommand) => {
    set({ loading: true, error: null });
    try {
      await contactsApi.createContactGroup(data);
      await get().fetchContactGroups(get().currentPage);
    } catch (error) {
      set({
        error: getErrorMessage(error, "Lỗi khi tạo Contact Group"),
        loading: false,
      });
      throw error;
    }
  },

  updateContactGroup: async (id: number, data: ContactGroupUpdateCommand) => {
    set({ loading: true, error: null });
    try {
      await contactsApi.updateContactGroup(id, data);
      await get().fetchContactGroups(get().currentPage);
    } catch (error) {
      set({
        error: getErrorMessage(error, "Lỗi khi cập nhật Contact Group"),
        loading: false,
      });
      throw error;
    }
  },

  deleteContactGroup: async (id: number) => {
    set({ loading: true, error: null });
    try {
      await contactsApi.deleteContactGroup(id);
      await get().fetchContactGroups(get().currentPage);
    } catch (error) {
      set({
        error: getErrorMessage(error, "Lỗi khi xóa Contact Group"),
        loading: false,
      });
      throw error;
    }
  },
}));
