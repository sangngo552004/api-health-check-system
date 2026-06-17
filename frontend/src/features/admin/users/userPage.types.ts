import { AdminUserDto } from "../../../types/workspace.types";

export type UserFormState = {
  username: string;
  email: string;
  phoneNumber: string;
  password: string;
  role: "SUPER_ADMIN" | "USER";
  isActive: boolean;
  requiresPasswordChange: boolean;
};

export const emptyUserForm: UserFormState = {
  username: "",
  email: "",
  phoneNumber: "",
  password: "",
  role: "USER",
  isActive: true,
  requiresPasswordChange: false,
};

export type UserRoleFilter = "ALL" | "SUPER_ADMIN" | "USER";
export type UserStatusFilter = "ALL" | "ACTIVE" | "INACTIVE";

export type UserModalProps = {
  editingUser: AdminUserDto | null;
  form: UserFormState;
  submitting: boolean;
  onChange: React.Dispatch<React.SetStateAction<UserFormState>>;
  onClose: () => void;
  onSubmit: (e: React.FormEvent) => Promise<void>;
};
