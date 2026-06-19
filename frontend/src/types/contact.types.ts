export interface ContactGroupDto {
  id: number;
  name: string;
  description?: string;
  workspaceId: number;
  isActive: boolean;
  emailAddresses: string[];
}

export interface ContactGroupCreateCommand {
  name: string;
  description?: string;
  emailAddresses: string[];
}

export interface ContactGroupUpdateCommand {
  id: number;
  name: string;
  description?: string;
  isActive?: boolean;
  emailAddresses: string[];
}
