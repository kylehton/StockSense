'use client';
import React, { useState } from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Modal from '@mui/material/Modal';
import GoogleSignIn from './GoogleSignIn';

const style = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: 400,
  bgcolor: 'rgba(31, 41, 55, 0.95)',
  backdropFilter: 'blur(8px)',
  boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
  p: 4,
  borderRadius: '1rem',
  border: '1px solid rgba(75, 85, 99, 0.4)',
};

export default function SignInModal() {
  const [open, setOpen] = useState(false);
  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  return (
    <div>
      <div id='button-wrapper' className='relative'>
        <Button 
          variant="outlined"
          className='relative px-6 py-2 text-white border border-indigo-500/50 hover:border-indigo-400 hover:bg-indigo-500/10 font-medium text-base transition-all duration-200'
          onClick={handleOpen}
        >
          Sign In
        </Button>
      </div>
      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
      >
        <Box sx={style}>
          <h2 className='text-3xl font-bold text-center mb-6 bg-gradient-to-r from-indigo-400 to-purple-400 bg-clip-text text-transparent'>
            Welcome!
          </h2>
          <GoogleSignIn />
        </Box>
      </Modal>
    </div>
  );
}