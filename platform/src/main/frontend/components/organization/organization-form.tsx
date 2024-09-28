import React, { useEffect, useState } from "react";
import { useDebouncedCallback } from "use-debounce";
import ImageUpload from "Frontend/components/core/image-upload";
import {
    Button,
    Checkbox, CheckboxCheckedChangedEvent,
    Select,
    SelectItem,
    SelectValueChangedEvent,
    TextField,
    TextFieldValueChangedEvent, Tooltip
} from "@vaadin/react-components";
import OrganizationDto from "Frontend/generated/rize/os/commons/organization/OrganizationDto";
import RegionDto from "Frontend/generated/rize/os/commons/region/RegionDto";
import {EndpointError} from "@vaadin/hilla-frontend";

interface OrganizationFormProps {
    organization?: OrganizationDto,
    regions?: RegionDto[],
    onConfirm: (organization: OrganizationDto) => Promise<void>,
    onCancel: () => void
}

const initialOrganization: OrganizationDto = {
    enabled: true
}

const nameTextFieldId = "organization-form-name-text-field";
const displayNameTextFieldId = "organization-form-display-name-text-field";

const nameHelperText = "The name of the organization will be used as identification in the system. It must be unique and can only contain lowercase letters, numbers and hyphens.";
const displayNameHelperText = "The display name will be used for presentation purposes of the organization. It can contain any characters.";
const regionHelperText = "The region in which the organization is located. The region is used to determine the data center where the organization's data will be stored.";

const imageUploadPlaceholder =
    <div className={"grid items-center justify-items-center sm:size-36 size-24 bg-slate-200 dark:bg-slate-600 select-none"}>
        <span className="material-icons text-slate-500 dark:text-slate-300 text-7xl sm:text-9xl">apartment</span>
    </div>;

const OrganizationForm: React.FC<OrganizationFormProps> = ({organization, regions, onConfirm, onCancel}) => {

    const [organizationToConfirm, setOrganizationToConfirm] = useState<OrganizationDto>(initialOrganization);
    const [nameError, setNameError] = useState<string | undefined>(undefined);
    const [displayNameError, setDisplayNameError] = useState<string | undefined>(undefined);
    const [regionError, setRegionError] = useState<string | undefined>(undefined);
    const [unexpectedError, setUnexpectedError] = useState<Error | undefined>(undefined);

    const focusTextField = (id: string) => {
        setTimeout(() => {
            document.getElementById(id)?.focus();
        }, 50);
    }

    const handleNameChanged = (e: TextFieldValueChangedEvent) => {
        setOrganizationToConfirm({...organizationToConfirm, name: e.detail.value});
        setNameError(undefined);
    }

    const handleDisplayNameChanged = (e: TextFieldValueChangedEvent) => {
        setOrganizationToConfirm({...organizationToConfirm, displayName: e.detail.value});
        setDisplayNameError(undefined);
    }

    const handleRegionChanged = (e: SelectValueChangedEvent) => {
        setOrganizationToConfirm({...organizationToConfirm, region: e.detail.value});
        setRegionError(undefined);
    }

    const handleEnabledChanged = (e: CheckboxCheckedChangedEvent) => {
        setOrganizationToConfirm({...organizationToConfirm, enabled: e.detail.value});
    }

    const handleConfirm = () => {
        setNameError(undefined);
        setDisplayNameError(undefined);
        setRegionError(undefined);

        if (!regions || regions.length === 0)
            organizationToConfirm.region = "default";

        onConfirm(organizationToConfirm)
            .catch(handleConfirmError);
    }

    const handleCancel = () => {
        onCancel?.();
    }

    const handleConfirmError = (error : Error) => {
        if (error instanceof EndpointError) {
            if (error.type === "rize.os.platform.organization.OrganizationAlreadyExistsException") {
                setNameError("Organization with this name already exists");
                return;
            }

            if (error.type === "rize.os.platform.organization.OrganizationConstraintException") {
                // @ts-ignore
                error.detail.forEach((violation: string) => {
                    if (violation.startsWith("name"))
                        setNameError("Invalid name");
                    if (violation.startsWith("displayName"))
                        setDisplayNameError("Display name cannot be empty");
                    if (violation.startsWith("region"))
                        setRegionError("Region is required");
                });
                return;
            }
        }

        setUnexpectedError(error);
    }

    const getRegionItems = (): SelectItem[] => {
        return regions?.map(region => ({ value: region.name, label: region.displayName })) || [];
    }

    useEffect(() => {
        focusTextField(nameTextFieldId);
    }, []);

    useEffect(() => {
        if (organization) {
            setOrganizationToConfirm(organization);
            focusTextField(displayNameTextFieldId);
        }
    }, [organization]);

    useEffect(() => {
        if (nameError)
            focusTextField(nameTextFieldId);
    }, [nameError]);

    useEffect(() => {
        if (displayNameError && !nameError)
            focusTextField(displayNameTextFieldId);
    }, [displayNameError]);

    return (
        <div className={"flex flex-col gap-2"}>
            <section className={"flex flex-row gap-8"}>
                <ImageUpload className={"col-start-1 row-span-2"}
                             placeholder={imageUploadPlaceholder}/>

                <div className={"grow flex flex-col gap-2"}>
                    <TextField id={nameTextFieldId}
                               value={organizationToConfirm?.name}
                               label={"Name"}
                               onValueChanged={useDebouncedCallback(handleNameChanged, 50)}
                               allowedCharPattern={"[a-z0-9-]"}
                               errorMessage={nameError}
                               invalid={!!nameError}
                    >
                        <span id={"name-text-field-info"} className={"material-icons text-2xl dark:text-slate-300 cursor-auto"} slot={"suffix"}>info</span>
                        <Tooltip for={"name-text-field-info"} text={nameHelperText} position={"bottom-end"} />
                    </TextField>

                    <TextField id={displayNameTextFieldId}
                               value={organizationToConfirm?.displayName}
                               label={"Display name"}
                               onValueChanged={useDebouncedCallback(handleDisplayNameChanged, 50)}
                               errorMessage={displayNameError}
                               invalid={!!displayNameError}
                    >
                        <span id={"display-name-text-field-info"} className={"material-icons text-2xl dark:text-slate-300 cursor-auto"} slot={"suffix"}>info</span>
                        <Tooltip for={"display-name-text-field-info"} text={displayNameHelperText} position={"bottom-end"} />
                    </TextField>

                    <Select label={"Region"}
                            value={organizationToConfirm?.region}
                            items={getRegionItems()}
                            disabled={regions?.length === 0}
                            className={regions?.length === 0 ? "hidden " : ""}
                            onValueChanged={useDebouncedCallback(handleRegionChanged, 50)}
                            errorMessage={regionError}
                            invalid={!!regionError}
                    >
                        <span id={"region-select-info"} className={"material-icons text-2xl dark:text-slate-300 mr-1 cursor-auto"} slot={"prefix"}>globe</span>
                    </Select>
                    <Tooltip for={"region-select-info"} text={regionHelperText} position={"bottom-start"} />

                    <Checkbox label={"Enabled"}
                              checked={organizationToConfirm?.enabled}
                              className={"w-min pt-2"}
                              onCheckedChanged={useDebouncedCallback(handleEnabledChanged, 50)}/>
                </div>
            </section>

            <section className={"flex flex-row-reverse gap-2 mt-2"}>
                <Button theme={"primary"} onClick={handleConfirm}>
                    <span className={"material-icons text-xl max-w-6 dark:text-slate-50"} slot={"prefix"}>save</span>
                    <span>Save</span>
                </Button>
                <Button onClick={handleCancel}>
                    <span>Cancel</span>
                </Button>
            </section>

            { unexpectedError &&
                <section className={"mt-2"}>
                    <div className={"text-red-500 leading-2 text-sm"}>
                        An unexpected error occurred while saving the organization. <a className={"underline cursor-pointer"} onClick={handleConfirm}>Click here</a> to try again!
                    </div>
                </section>
            }
        </div>
    )
}

export default OrganizationForm;