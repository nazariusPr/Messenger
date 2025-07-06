import "styled-components";

declare module "styled-components" {
  export interface DefaultTheme {
    colors: {
      primary: string;
      secondary: string;
      background: string;
      surface: string;
      text: string;
      muted: string;
      border: string;
      error: string;
      success: string;
      warning: string;
      info: string;
    };
    fontSizes: {
      small: string;
      medium: string;
      large: string;
    };
    spacing: {
      xs: string;
      sm: string;
      md: string;
      lg: string;
    };
    borderRadius: {
      sm: string;
      md: string;
      lg: string;
    };
    breakpoints: {
      mobile: string;
      tablet: string;
      desktop: string;
    };
    zIndex: {
      hide: number;
      auto: string;
      base: number;
      dropdown: number;
      sticky: number;
      fixed: number;
      modalBackdrop: number;
      modal: number;
      tooltip: number;
    };
  }
}
