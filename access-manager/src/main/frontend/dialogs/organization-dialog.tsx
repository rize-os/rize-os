import { Notification, Dialog, Button, TextField, Icon, DialogOpenedChangedEvent } from "@vaadin/react-components";
import  Organization from "Frontend/generated/rize/os/access/manager/organization/Organization";
import React, {useEffect, useState} from "react";
import Apartment from "@mui/icons-material/Apartment";
import {OrganizationEndpoint} from "Frontend/generated/endpoints";

interface OrganizationDialogProps {
    mode?: "edit" | "create"
    organization?: Organization,
    opened?: boolean,
    onClose?: () => void,
    onCreate?: (organization: Organization) => void,
    onUpdate?: (organization: Organization) => void
}

const OrganizationDialog: React.FC<OrganizationDialogProps> = (props) => {

    const nameRegex = /^[a-z0-9][a-z0-9-]{0,62}[a-z0-9]$/;

    const [opened, setOpened] = useState<boolean>(false);
    const [isSaving, setIsSaving] = useState<boolean>(false);

    const [displayName, setDisplayName] = useState<string | undefined>(undefined);
    const [displayNameError, setDisplayNameError] = useState<string>("");
    const [name, setName] = useState<string | undefined>(undefined);
    const [nameError, setNameError] = useState<string>("");
    const [nameEditedManually, setNameEditedManually] = useState<boolean>(false);

    useEffect(() => {
        setOpened(props.opened?? true);
    }, [props.opened]);

    useEffect(() => {
        if (opened) {
            focusTextField("display-name-text-field");
            setDisplayName(props.organization?.displayName ?? undefined);
            setName(props.organization?.name ?? undefined);
            setNameEditedManually(!!props.organization);
        }
    }, [props.organization, opened]);

    useEffect(() => {
        const delayDebounceFn = setTimeout(validateName, 500);
        return () => clearTimeout(delayDebounceFn);
    }, [name]);

    useEffect(() => {
        if (!nameEditedManually && displayName)
            generateName(displayName);

        const delayDebounceFn = setTimeout(validateDisplayName, 500);
        return () => clearTimeout(delayDebounceFn);
    }, [displayName]);


    const openErrorNotification = (message: string) => {
        Notification.show(message, {theme: "error", position: "bottom-end", duration: 8000})
    }

    const validateName = async (): Promise<boolean> => {
        if (name === undefined)
            return false;

        if (!nameRegex.test(name)) {
            setNameError("Name must start and end with a letter or number and must be between 2 and 64 characters long");
            return false;
        }

        try {
            if (name !== props.organization?.name && await OrganizationEndpoint.nameExists(name)) {
                setNameError("Name already exists");
                return false;
            }
        }
        catch (e) {
            openErrorNotification("Unexpected error occurred while validating organization name");
            return false;
        }

        setNameError("");
        return true;
    }

    const validateDisplayName = async (): Promise<boolean> => {
        if (displayName === undefined)
            return false;

        if (displayName.length === 0) {
            setDisplayNameError("Display name is required");
            return false;
        }

        setDisplayNameError("");
        return true;
    }

    const generateName = (displayName: string) => {
        setName(displayName
            .replace(/[^a-zA-Z0-9\s-_]/g, '')
            .replace(/[\s_]+/g, '-')
            .replace(/-+/g, '-')
            .toLowerCase());
    }

    const focusTextField = (id: string) => {
        setTimeout(() => {
            document.getElementById(id)?.focus();
        }, 50);
    }

    const handleOpenedChanged = (e: DialogOpenedChangedEvent) => {
        if (!e.detail.value)
            props.onClose?.();
    };

    const handleSave = async () => {
        if (name === undefined || displayName === undefined) {
            setName(name ?? "");
            setDisplayName(displayName ?? "");
            return;
        }

        if (!await validateName() || !await validateDisplayName())
            return;

        setIsSaving(true);

        try {
            if (props.mode === "create")
                await createOrganization();
            else
                await updateOrganization();
        }
        catch (e) {
            openErrorNotification("Unexpected error occurred while saving organization");
            setIsSaving(false);
            return;
        }

        setIsSaving(false);
        setOpened(false)
    };

    const createOrganization = async () => {
        if (!name || !displayName)
            return;

        const organization = await OrganizationEndpoint.create(displayName, name);
        props.onCreate?.(organization);
    }

    const updateOrganization = async () => {
        if (!props.organization || !name || !displayName)
            return;

        let organization= props.organization;
        organization.name = name;
        organization.displayName = displayName;
        organization.aliases = [name];
        let updatedOrganization = await OrganizationEndpoint.update(organization);

        props.onUpdate?.(updatedOrganization);
    }

    return (
        <Dialog onOpenedChanged={handleOpenedChanged}
                opened={opened}
                noCloseOnEsc
                noCloseOnOutsideClick
                footerRenderer={() => (
                    <>
                        <Button onClick={() => setOpened(false)}>Cancel</Button>
                        <Button onClick={handleSave} theme={"primary"}>
                            {isSaving ? <Icon icon={"vaadin:spinner"} className={"animate-spin"}/> : <></>}
                            Save
                        </Button>
                    </>
                )}>
            <div className={"grid gap-x-6 gap-y-4"}>
                <section className={"col-span-2"}>
                    <span className={"text-2xl font-bold"}>{props.mode === "create" ? "Create Organization" : "Edit Organization"}</span>
                </section>

                <section className={"content-center"}>
                    <div className={"grid bg-gray-300 rounded-lg w-28 h-28 content-center justify-center"}>
                        <Apartment className={"text-gray-500"} style={{transform: "scale(3.2)"}}/>
                    </div>
                </section>

                <section className={"flex flex-col w-72"}>
                    <TextField id={"display-name-text-field"}
                               value={displayName}
                               label={"Display Name"}
                               invalid={displayNameError.length > 0}
                               errorMessage={displayNameError}
                               onValueChanged={e => setDisplayName(e.detail.value)}
                               allowedCharPattern={"[a-zA-Z0-9-_&+./ ]"}
                               className={"pt-0"}/>

                    <TextField id={"name-text-field"}
                               value={name}
                               label={"Name"}
                               invalid={nameError.length > 0}
                               errorMessage={nameError}
                               onValueChanged={e => setName(e.detail.value)}
                               onFocus={() => setNameEditedManually(true)}
                               allowedCharPattern={"[a-z0-9-]"}/>
                </section>

                <section className={"col-span-2"}>
                    {props.organization?.enabled ?
                        <span {...{theme: 'badge success pill'}} className={"flex-shrink"}>Active Subscription</span> :
                        <></>}
                </section>
            </div>
        </Dialog>
    );
}

export default OrganizationDialog;