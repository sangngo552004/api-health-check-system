import type { AppRole } from "../../../context/auth-context";
import { AdminUserDto } from "../../../types/workspace.types";

export type UserFormState = {
  username: string;
  email: string;
  phoneNumber: string;
  password: string;
  role: AppRole;
  isActive: boolean;
};

export const emptyUserForm: UserFormState = {
  username: "",
  email: "",
  phoneNumber: "",
  password: "",
  role: "USER",
  isActive: true,
};

export type UserRoleFilter = "ALL" | AppRole;
export type UserStatusFilter = "ALL" | "ACTIVE" | "INACTIVE";

export type UserModalProps = {
  editingUser: AdminUserDto | null;
  form: UserFormState;
  phoneError: string | null;
  submitting: boolean;
  onChange: React.Dispatch<React.SetStateAction<UserFormState>>;
  onClose: () => void;
  onSubmit: (e: React.FormEvent) => Promise<void>;
};
