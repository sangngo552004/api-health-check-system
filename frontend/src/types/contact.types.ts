export interface ContactGroupDto {
  id: number;
  name: string;
  description: string;
  isActive: boolean;
  userIds: number[];
  emailAddresses: string[];
  webhookUrls: string[];
  createdAt: string;
  updatedAt: string;
}

export interface ContactGroupCreateCommand {
  name: string;
  description?: string;
  isActive: boolean;
  userIds: number[];
  emailAddresses: string[];
  webhookUrls: string[];
}

export interface ContactGroupUpdateCommand extends ContactGroupCreateCommand {
  id: number;
}
