import { WorkspaceDto } from "../../../types/workspace.types";

export type WorkspaceFormState = {
  name: string;
  description: string;
  slug: string;
  ownerId: string;
  isActive: boolean;
};

export const emptyWorkspaceForm: WorkspaceFormState = {
  name: "",
  description: "",
  slug: "",
  ownerId: "",
  isActive: true,
};

export type WorkspaceStatusFilter = "ALL" | "ACTIVE" | "INACTIVE";

export type WorkspaceModalProps = {
  editingWorkspace: WorkspaceDto | null;
  form: WorkspaceFormState;
  submitting: boolean;
  onChange: React.Dispatch<React.SetStateAction<WorkspaceFormState>>;
  onClose: () => void;
  onSubmit: (e: React.FormEvent) => Promise<void>;
};
