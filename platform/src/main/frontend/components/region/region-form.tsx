import React, { useEffect, useState } from "react";
import { Button, TextField, TextFieldValueChangedEvent } from "@vaadin/react-components";
import RegionDto from "Frontend/generated/rize/os/commons/region/RegionDto";
import { EndpointError } from "@vaadin/hilla-frontend";

interface RegionFormProps {
    region?: RegionDto,
    onConfirm: (region: RegionDto) => Promise<void>,
    onCancel?: () => void
}

const nameTextFieldId = "region-form-name-text-field";
const displayNameTextFieldId = "region-form-display-name-text-field";

const RegionForm: React.FC<RegionFormProps> = ({ region, onConfirm, onCancel }) => {

    const [regionToConfirm, setRegionToConfirm] = useState<RegionDto>({});
    const [nameError, setNameError] = useState<string | undefined>(undefined);
    const [displayNameError, setDisplayNameError] = useState<string | undefined>(undefined);
    const [unexpectedError, setUnexpectedError] = useState<Error | undefined>(undefined);

    const focusTextField = (id: string) => {
        setTimeout(() => {
            document.getElementById(id)?.focus();
        }, 50);
    }

    const handleNameChanged = (e: TextFieldValueChangedEvent) => {
        setRegionToConfirm({...regionToConfirm, name: e.detail.value});
        setNameError(undefined);
    }

    const handleDisplayNameChanged = (e: TextFieldValueChangedEvent) => {
        setRegionToConfirm({...regionToConfirm, displayName: e.detail.value});
        setDisplayNameError(undefined);
    }

    const handleConfirm = () => {
        setDisplayNameError(undefined);
        setNameError(undefined);

        onConfirm(regionToConfirm)
            .catch(handleConfirmError);
    }

    const handleCancel = () => {
        onCancel?.();
    }

    const handleConfirmError = (error : Error) => {
        if (error instanceof EndpointError) {
            if (error.type === "rize.os.platform.region.RegionAlreadyExistsException") {
                setNameError("Region with this name already exists");
                return;
            }

            if (error.type === "rize.os.platform.region.RegionConstraintException") {
                // @ts-ignore
                error.detail.forEach((violation: string) => {
                    if (violation.startsWith("name"))
                        setNameError("Invalid name");
                    if (violation.startsWith("displayName"))
                        setDisplayNameError("Display name cannot be empty");
                });
                return;
            }
        }

        setUnexpectedError(error);
    }

    useEffect(() => {
        focusTextField(nameTextFieldId);
    }, []);

    useEffect(() => {
        if (region)
            setRegionToConfirm(region);
    }, [region]);

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
            <section>
                <TextField id={nameTextFieldId}
                           className={"w-full"}
                           label={"Name"}
                           onValueChanged={handleNameChanged}
                           disabled={region !== undefined}
                           allowedCharPattern={"[a-z0-9-]"}
                           errorMessage={nameError}
                           invalid={!!nameError}
                >
                    {region?.name}
                </TextField>
            </section>

            <section>
                <TextField id={displayNameTextFieldId}
                           className={"w-full"}
                           label={"Display Name"}
                           onValueChanged={handleDisplayNameChanged}
                           errorMessage={displayNameError}
                           invalid={!!displayNameError}
                >
                    {region?.displayName}
                </TextField>
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
                        An unexpected error occurred while saving the region. <a className={"underline cursor-pointer"} onClick={handleConfirm}>Click here</a> to try again!
                    </div>
                </section>
            }
        </div>
    )
}

export default RegionForm;
